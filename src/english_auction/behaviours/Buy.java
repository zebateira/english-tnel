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
	public static final int	DEFINE_BID	= 1;
	public static final int	SEND_CFP		= 2;
	public static final int	GET_PROPOSALS	= 3;
	public static final int	SEND_REPLIES	= 4;

	boolean	objective	= false;
	int						numberOfBidders;
	int						currentBestBid;
	AID						currentBestBidder;
	DFAgentDescription[]	bidders;

	MessageTemplate			replyMessage	= MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)), MessageTemplate.MatchInReplyTo(item.toString()));

	public Buy(TradingAgent agent, TradableItem item) {
		super(agent, item);
		state = DEFINE_BID;
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

			Thread.sleep(5);
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
		System.out.println("NEW AUCTION");
	}

	/**
	 * Enviar pedidos 
	 * -> CFP(n)
	 * */
	public void state2() throws FIPAException {
		BuyerAgent bu = (BuyerAgent) myTradingAgent;
		bidders = this.search(bu.getMySellerType());
		for (DFAgentDescription agent : bidders)
			this.reply(agent.getName(), "" + currentBestBid, ACLMessage.CFP, item.toString());
		numberOfBidders = bidders.length;
		state = GET_PROPOSALS;

		System.out.println("AUCTION HAS " + numberOfBidders + " BIDDERS");
	}

	/**
	 * Receber Propostas
	 * <- Propose(n - k) 
	 * */
	public void state3() {
		ACLMessage message = this.myAgent.receive(replyMessage);
		if (message == null)
			return;

		if (message.getPerformative() == ACLMessage.PROPOSE) {
			int proposal = Integer.parseInt(message.getContent());

			System.out.println("NEW BID BY " + message.getSender().getLocalName() + " WITH " + proposal);

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
