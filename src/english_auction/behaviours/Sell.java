package english_auction.behaviours;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Calendar;

import english_auction.JadeManager;
import english_auction.agents.TradingAgent;
import english_auction.goods.AgentStorage;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public class Sell extends Transaction {
	public static final int	GET_CFP					= 1;
	public static final int	DEFINE_BID				= 2;
	public static final int	SEND_PROPOSAL			= 3;
	public static final int	GET_REPLY				= 4;

	int						sugestedBid;
	AID						auctioneer				= null;
	int						myBid;
	long					timeout;
	int						round;
	boolean					reserved;

	MessageTemplate			cfpMessage				= MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchInReplyTo(item.toString()));
	MessageTemplate			replyProposalMessage	= MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL)), MessageTemplate.MatchInReplyTo(item.toString()));

	public Sell(TradingAgent agent, TradableItem item) {
		super(agent, item);
		state = GET_CFP;
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
					state2();
					break;
				case SEND_PROPOSAL:
					state3();
					break;
				case GET_REPLY:
					state4();
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
		if (message == null)
			return;

		round = Integer.parseInt(message.getContent().split("@")[0]);
		sugestedBid = Integer.parseInt(message.getContent().split("@")[1]);
		auctioneer = message.getSender();

		//System.out.println(myTradingAgent.getLocalName() + " received from " + auctioneer.getLocalName() + " for " + item.toString() + " with " + sugestedBid);
		state = DEFINE_BID;
		timeout = Calendar.getInstance().getTimeInMillis() + JadeManager.TIMEOUT;
	}

	/**
	 * Definir valor da bid
	 * */
	public void state2() {
		myBid = myTradingAgent.buyBid(item, sugestedBid, round);
		state = SEND_PROPOSAL;
	}

	/**
	 * Enviar Proposal
	 * */
	public void state3() {
		AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;

		boolean willSell = myTradingAgent.shouldSell(item, myBid);

		synchronized (storage) {
			if ((reserved || storage.hasAllDependencies(item)) && willSell) {
				this.reply(auctioneer, "" + myBid, ACLMessage.PROPOSE, item.toString());
				if (!reserved) {
					storage.removeItem(item);
					reserved = true;
				}
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
		ACLMessage message = this.myTradingAgent.receive(replyProposalMessage);

		if (message != null) {
			if (round <= 0 && reserved) {
				if (message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {

					myTradingAgent.gold += myBid;
					
					System.err.println(this.myTradingAgent.getLocalName() + " Sold " + item.name() + " for " + myBid + "$ to " + auctioneer.getLocalName() + " , now have" + myTradingAgent.storage);
					System.err.flush();

				}
				else if (message.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
					synchronized (myTradingAgent.storage) {
						myTradingAgent.storage.restoreItem(item);
					}
				}
				reserved = false;
			}
			state = GET_CFP;
		}

		if (timeout < Calendar.getInstance().getTimeInMillis())
			state = GET_CFP;
	}

	@Override
	public boolean done() {
		return !reserved && myTradingAgent.sellerOut();
	}

}
