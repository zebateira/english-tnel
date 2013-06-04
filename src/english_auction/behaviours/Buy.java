package english_auction.behaviours;

import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import english_auction.JadeManager;
import english_auction.agents.BuyerAgent;
import english_auction.agents.TradingAgent;
import english_auction.goods.AgentStorage;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public class Buy extends Transaction {
	public static final int	DEFINE_BID			= 1;
	public static final int	SEND_CFP		= 2;
	public static final int	GET_PROPOSALS	= 3;
	public static final int	SEND_REPLIES	= 4;


	int						currentBestBid;
	AID						currentBestBidder	= null;
	ArrayList<AID>			bidders;
	ArrayList<AID>			newBidders;
	long					timeout;
	int						quiters;
	int						round;
	boolean					newAuction;


	MessageTemplate			replyMessage	= MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)), MessageTemplate.MatchInReplyTo(item.toString()));

	public Buy(TradingAgent agent, TradableItem item) {
		super(agent, item);
		state = DEFINE_BID;
		newAuction = true;
	}

	@Override
	public void action() {
		try {
			switch (state) {
				case DEFINE_BID:
					state1();
					break;
				case SEND_CFP:
					state2();
					break;
				case GET_PROPOSALS:
					state3();
					break;
				case SEND_REPLIES:
					state4();
					break;
			}

			Thread.sleep(10);
		}
		catch (InterruptedException | FIPAException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Definir valor inicial do leilão
	 * @throws FIPAException 
	 * */
	public void state1() {
		if (newAuction) {
			round = JadeManager.ROUNDS + 1;
			System.out.println("NEW AUCTION " + myTradingAgent.getLocalName() + " -> " + item.toString());
			currentBestBid = myTradingAgent.startingBid(item);
			newAuction = false;
		}

		round--;
		currentBestBidder = null;
		quiters = 0;
		state = SEND_CFP;
		System.out.println(round + "ºROUND " + myTradingAgent.getLocalName() + " -> " + item.toString());
	}

	/**
	 * Enviar pedidos 
	 * -> CFP(n)
	 * */
	public void state2() throws FIPAException {
		BuyerAgent bu = (BuyerAgent) myTradingAgent;

		bidders = new ArrayList<AID>();
		newBidders = new ArrayList<AID>();
		for (DFAgentDescription agent : this.search(bu.getMySellerType())) {
			bidders.add(agent.getName());
			this.reply(agent.getName(), round + "@" + currentBestBid, ACLMessage.CFP, item.toString());
		}
		state = GET_PROPOSALS;
		timeout = Calendar.getInstance().getTimeInMillis() + JadeManager.TIMEOUT;
	}

	/**
	 * Receber Propostas
	 * <- Propose(n - k) 
	 * */
	public void state3() {
		ACLMessage message = this.myTradingAgent.receive(replyMessage);

		if (message != null) {
			if (message.getPerformative() == ACLMessage.PROPOSE && bidders.contains(message.getSender())) {
				newBidders.add(message.getSender());

				int proposal = Integer.parseInt(message.getContent());

				System.out.println(myTradingAgent.getLocalName() + " -> NEW BID BY " + message.getSender().getLocalName() + " WITH " + proposal);

				if (proposal <= currentBestBid) {
					currentBestBid = proposal;
					currentBestBidder = message.getSender();
				}

			}
			else if (message.getPerformative() == ACLMessage.INFORM) {
				quiters++;
			}
		}

		if (bidders.size() == newBidders.size() + quiters || timeout < Calendar.getInstance().getTimeInMillis()) {
			state = SEND_REPLIES;
			bidders = newBidders;

			if (newBidders.size() == 0) {
				newAuction = true;
				state = DEFINE_BID;
			}

		}
	}

	/**
	 * Responder às Propostas
	 * -> Reject (n - k) Fim de turno, Volta a state2
	 * -> Accept(1) / Reject (n - k - 1) Fim de leilão, Volta a state1
	 * */
	public void state4() {
		boolean willBuy = myTradingAgent.shouldBuy(item, currentBestBid);
		
		if (round <= 0 && currentBestBidder != null && willBuy) {
			synchronized (myTradingAgent.storage) {
				AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;
				storage.addItem(item);
				myTradingAgent.gold -= currentBestBid;

				System.err.println(this.myTradingAgent.getLocalName() + " Bought " + item.name() + " for " + currentBestBid + "$ to " + currentBestBidder.getLocalName() + " , now have" + storage);
				System.err.flush();

				this.reply(currentBestBidder, "" + currentBestBid, ACLMessage.ACCEPT_PROPOSAL, item.toString());
			}
		}

		if (round <= 0)
			newAuction = true;

		for (AID agent : bidders) {
			if (!agent.equals(currentBestBidder))
				this.reply(agent, "" + currentBestBid, ACLMessage.REJECT_PROPOSAL, item.toString());
		}

		state = DEFINE_BID;
	}

	@Override
	public boolean done() {
		return newAuction && myTradingAgent.buyerOut();
	}
}
