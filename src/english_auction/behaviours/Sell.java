package english_auction.behaviours;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import english_auction.JadeManager;
import english_auction.agents.TradingAgent;
import english_auction.goods.AgentStorage;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public class Sell extends Transaction {
	public static final int	GET_CFP			= 1;
	public static final int	DEFINE_BID		= 2;
	public static final int	SEND_PROPOSAL	= 3;
	public static final int	GET_REPLY		= 4;

	int						sugestedBid;
	int						myBid;
	ArrayList<AID>			auctioneers;
	long					timeout;
	long					auctionTimeout;
	int						iterations;
	boolean					newAuction				= true;

	MessageTemplate			cfpMessage				= MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchInReplyTo(item.toString()));
	MessageTemplate			replyProposalMessage	= MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL)), MessageTemplate.MatchInReplyTo(item.toString()));


	public Sell(TradingAgent agent, TradableItem item) {
		super(agent, item);
		state = GET_CFP;
		auctioneers = new ArrayList<AID>();
	}

	@Override
	public void action() {
		//System.out.println(myTradingAgent.getLocalName() + " -> " + item.toString() + " -> " + state);
		try {
			switch (state) {
				case GET_CFP:
					state1();
					break;
				case DEFINE_BID:
					for (AID auc : auctioneers)
						state2(auc);
					break;
				case SEND_PROPOSAL:
					for (AID auc : auctioneers)
						state3(auc);
					break;
				case GET_REPLY:
					for (AID auc : auctioneers)
						state4(auc);
					break;
			}

			Thread.sleep(10);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receber CFP
	 * */
	public void state1() {
		ACLMessage message = this.myTradingAgent.receive(cfpMessage);

		if (message != null) {
			String[] msg = message.getContent().split("@");
			iterations = Integer.parseInt(msg[1]);

			sugestedBid = Integer.parseInt(msg[0]);
			AID auctioneer = message.getSender();

			if (iterations == 2) {
				if (newAuction) {
					auctionTimeout = Calendar.getInstance().getTimeInMillis() + JadeManager.AUCTION_TIMEOUT;
					newAuction = false;
				}

				auctioneers.add(auctioneer);
				System.out.println(myTradingAgent.getLocalName() + " received from " + auctioneer.getLocalName() + " for " + item.toString() + " with " + sugestedBid);
			}
		}

		if (auctionTimeout < Calendar.getInstance().getTimeInMillis()) {
			state = DEFINE_BID;
			timeout = Calendar.getInstance().getTimeInMillis() + JadeManager.TIMEOUT;
		}
	}

	/**
	 * Definir valor da bid
	 * */
	public void state2(AID auctioneer) {
		myBid = myTradingAgent.buyBid(item, sugestedBid, iterations);
		state = SEND_PROPOSAL;
	}

	/**
	 * Enviar Proposal
	 * */
	public void state3(AID auctioneer) {
		AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;

		boolean willSell = myTradingAgent.shouldSell(item, myBid, iterations);

		synchronized (storage) {
			if (storage.hasAllDependencies(item) && willSell) {
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
	public void state4(AID auctioneer) {
		ACLMessage message = this.myTradingAgent.receive(replyProposalMessage);

		if (message != null) {
			if (message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL && iterations <= 0) {

				System.err.println(this.myTradingAgent.getLocalName() + " Sold " + item.name() + " for " + myBid + "$ to " + auctioneer.getLocalName() + " , now have" + myTradingAgent.storage);
				System.err.flush();
				newAuction = true;
			}
			else if (message.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
				AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;
				synchronized (storage) {
					storage.restoreItem(item);
				}
			}
			state = GET_CFP;
		}

		if (timeout < Calendar.getInstance().getTimeInMillis()) {
			AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;
			synchronized (storage) {
				storage.restoreItem(item);
			}
			state = GET_CFP;
			newAuction = true;
		}
	}

	@Override
	public boolean done() {
		return this.myTradingAgent.sellerOut(item);
	}

}
