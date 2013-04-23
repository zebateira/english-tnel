package english_auction.behaviours;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

import english_auction.agents.BuyerAgent;
import english_auction.agents.TradingAgent;
import english_auction.goods.Item;

@SuppressWarnings("serial")
public class Buy extends Transaction {

	public Buy(Item item) {
		super(item, MessageTemplate.MatchInReplyTo(item.toString()));
	}

	@Override
	public void action() {

		try {
			Thread.sleep(5);
		}
		catch (InterruptedException e1) {
		}

		try {
			TradingAgent myTradingAgent = (TradingAgent) this.myAgent;

			DFAgentDescription[] sellers = this.search(((BuyerAgent) myTradingAgent).getMySellerType());
			
			for (DFAgentDescription agent : sellers) {
				this.reply(agent.getName(), "", ACLMessage.CFP, item.toString());
				ACLMessage message = this.myAgent.receive(messageTemplate);

				if (message == null)
					continue;

				if (message.getPerformative() == ACLMessage.PROPOSE) {
					synchronized (myTradingAgent.storage) {
						HashMap<Item, Integer> storage = myTradingAgent.storage;
						storage.put(item, storage.get(item) + 1);

						System.err.println(this.myAgent.getLocalName() + " Bought " + item.name() + ", now have" + storage);
						System.err.flush();
					}
				} else if (message.getPerformative() == ACLMessage.INFORM) {
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

}
