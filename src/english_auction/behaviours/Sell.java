package english_auction.behaviours;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

import english_auction.agents.TradingAgent;
import english_auction.goods.AgentStorage;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public class Sell extends Transaction {
	public static final int	GET_CFP			= 1;
	public static final int	DEFINE_BID		= 2;
	public static final int	SEND_PROPOSAL	= 3;
	public static final int	GET_REPLY		= 4;

	boolean					objective		= false;

	int						sugestedBid;
	AID						auctioneer		= null;
	int						myBid;

	MessageTemplate			cfpMessage				= MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchInReplyTo(item.toString()));
	MessageTemplate			replyProposalMessage	= MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL)), MessageTemplate.MatchInReplyTo(item.toString()));


	public Sell(TradingAgent agent, TradableItem item) {
		super(agent, item);
		state = GET_CFP;
	}

	@Override
	public void action() {
		try {
			switch (state) {
				case GET_CFP:
					state1();
					break;
				case DEFINE_BID:
					state2();
					break;
				case SEND_PROPOSAL:
					state3();
					break;
				case GET_REPLY:
					state4();
					break;
			}

			Thread.sleep(4);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receber CFP
	 * */
	public void state1() {
		ACLMessage message = this.myAgent.receive(cfpMessage);
		if (message == null)
			return;

		sugestedBid = Integer.parseInt(message.getContent());
		auctioneer = message.getSender();

		state = DEFINE_BID;
	}

	/**
	 * Definir valor da bid
	 * */
	public void state2() {
		myBid = sugestedBid - new Random().nextInt(50);

		state = SEND_PROPOSAL;
	}

	/**
	 * Enviar Proposal
	 * */
	public void state3() {
		AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;
		synchronized (storage) {
			if (storage.hasAllDependencies(item)) {

				this.reply(auctioneer, "" + myBid, ACLMessage.PROPOSE, item.toString());

				if (storage.hasAllDependencies(item))
					storage.removeItem(item);
			}
			else
				this.reply(auctioneer, "", ACLMessage.INFORM, item.toString());
		}

		state = GET_REPLY;
	}

	/**
	 * Receber Resposta
	 * */
	public void state4() {
		ACLMessage message = this.myAgent.receive(replyProposalMessage);
		if (message == null)
			return;

		if (message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {

			System.err.println(this.myAgent.getLocalName() + " Sold " + item.name() + " for " + myBid + "$ to " + auctioneer.getLocalName() + " , now have" + myTradingAgent.storage);
			System.err.flush();

		}
		else if (message.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
			AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;
			synchronized (storage) {
				storage.restoreItem(item);
			}
		}

		state = GET_CFP;
	}

	@Override
	public boolean done() {
		return objective;
	}

}
