package english_auction.behaviours;

import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import english_auction.agents.BuyerAgent;
import english_auction.agents.TradingAgent;
import english_auction.goods.AgentStorage;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public class Buy extends Transaction {
	public static final int	START_AUCTION	= 1;
	public static final int	SEND_CFP		= 2;
	public static final int	GET_PROPOSALS	= 3;
	public static final int	SEND_REPLIES	= 4;

	boolean	objective	= false;
	int						numberOfBidders;
	int						currentBestBid;
	AID						currentBestBidder;
	DFAgentDescription[]	bidders;

	TradingAgent			myTradingAgent;

	public Buy(TradableItem item) {
		super(item, MessageTemplate.MatchInReplyTo(item.toString()));
		state = START_AUCTION;
		myTradingAgent = (TradingAgent) this.myAgent;
	}

	@Override
	public void action() {
		try {
			switch (state) {
				case START_AUCTION:
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

			Thread.sleep(1000);
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
		currentBestBid = 100;
		currentBestBidder = null;
		state = SEND_CFP;
	}

	/**
	 * Enviar pedidos 
	 * -> CFP(n)
	 * */
	public void state2() throws FIPAException {
		bidders = this.search(((BuyerAgent) myTradingAgent).getMySellerType());
		for (DFAgentDescription agent : bidders)
			this.reply(agent.getName(), "", ACLMessage.CFP, item.toString());
		numberOfBidders = bidders.length;
		state = GET_PROPOSALS;
	}

	/**
	 * Receber Propostas
	 * <- Propose(n - k) 
	 * */
	public void state3() {
		ACLMessage message = this.myAgent.receive(messageTemplate);
		if (message == null)
			return;

		if (message.getPerformative() == ACLMessage.PROPOSE) {
			int proposal = Integer.parseInt(message.getContent());

			if (proposal < currentBestBid) {
				currentBestBid = proposal;
				currentBestBidder = message.getSender();
			}
		}
		else if (message.getPerformative() == ACLMessage.INFORM) {
			// Não tem nada
		}

		numberOfBidders--;
		if (numberOfBidders == 0)
			state = SEND_REPLIES;
	}

	/**
	 * Responder às Propostas
	 * -> Reject (n - k) Fim de turno, Volta a state2
	 * -> Accept(1) / Reject (n - k - 1) Fim de leilão, Volta a state1
	 * */
	public void state4() {
		if (currentBestBidder != null)
			synchronized (myTradingAgent.storage) {
				AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;
				storage.addItem(item);

				System.err.println(this.myAgent.getLocalName() + " Bought " + item.name() + " for " + currentBestBid + "$ to " + currentBestBidder.getLocalName() + " , now have" + storage);
				System.err.flush();
			}
		
		for (DFAgentDescription agent : bidders) {
			if (agent.getName().equals(currentBestBidder))
				this.reply(agent.getName(), "" + currentBestBid, ACLMessage.ACCEPT_PROPOSAL, item.toString());
			else
				this.reply(agent.getName(), "" + currentBestBid, ACLMessage.REJECT_PROPOSAL, item.toString());
		}

		objective = true;
	}


	@Override
	public boolean done() {
		if (objective)
			System.out.println("I'm out, bye bye");
		return objective;
	}

}
