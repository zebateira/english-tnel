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

	private static final boolean	NEXT_READY	= true;
	
	int						sugestedBid;
	int						myBid;
	ArrayList<AID>			auctioneers;
	long					timeout;
	long					auctionTimeout;
	int						roundNumber;
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
		
		// is behaviour ready for next state
		boolean nextReady = true;
		
		try {
			switch (state) { 
				case GET_CFP:
					state1();
					break;
				case DEFINE_BID:
					for (AID auc : auctioneers)
						nextReady &= state2(auc);
					
					if( nextReady )
						this.state = SEND_PROPOSAL;
//					System.out.println(this + " | DEFINE_BID | next state: " + state);
					break;
				case SEND_PROPOSAL:
					for (AID auc : auctioneers)
						nextReady &= state3(auc);
					
					if( nextReady )
						this.state = GET_REPLY;
//					System.out.println(this + " | SEND_PROPOSAL | next state: " + state);
					break;
				case GET_REPLY:
					for (AID auc : auctioneers)
						nextReady &= state4(auc);

					if( nextReady ) {
						this.state = GET_CFP;
//						auctioneers = new ArrayList<AID>();
					}
//					System.out.println(this + " | GET_REPLY | state: " + state);
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
			roundNumber = Integer.parseInt(msg[1]);

			sugestedBid = Integer.parseInt(msg[0]);
			AID auctioneer = message.getSender();

			if (roundNumber == 2) {
				if (newAuction) {
					auctionTimeout = Calendar.getInstance().getTimeInMillis() + JadeManager.AUCTION_TIMEOUT;
					newAuction = false;
					auctioneers = new ArrayList<AID>();
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
	 * @return 
	 * */
	public boolean state2(AID auctioneer) {
		myBid = myTradingAgent.buyBid(item, sugestedBid, roundNumber);
//		state = SEND_PROPOSAL;
		
		return NEXT_READY;
	}

	/**
	 * Enviar Proposal
	 * */
	public boolean state3(AID auctioneer) {
		AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;

		boolean willSell = myTradingAgent.shouldSell(item, myBid, roundNumber);

		synchronized (storage) {
			if (storage.hasAllDependencies(item) && willSell) {
				this.reply(auctioneer, "" + myBid, ACLMessage.PROPOSE, item.toString());

				if (storage.hasAllDependencies(item)) {
					storage.removeItem(item);
				}
			}
			else
				this.reply(auctioneer, "", ACLMessage.INFORM, item.toString());
		}

//		state = GET_REPLY;
		return NEXT_READY;
	}

	/**
	 * Receber Resposta
	 * */
	public boolean state4(AID auctioneer) {
		ACLMessage message = this.myTradingAgent.receive(replyProposalMessage);
		boolean isReady = false;

		if (message != null) {
			if (message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL && roundNumber <= 0) {

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
//			state = GET_CFP;
			isReady = true;
		}

		if (timeout < Calendar.getInstance().getTimeInMillis()) {
			AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;
			synchronized (storage) {
				storage.restoreItem(item);
			}
//			state = GET_CFP;
			newAuction = true;
			isReady = true;
		}
		
		return isReady;
	}

	@Override
	public boolean done() {
		return this.myTradingAgent.sellerOut(item);
	}

}
