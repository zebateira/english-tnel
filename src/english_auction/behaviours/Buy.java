package english_auction.behaviours;

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

	public Buy(TradableItem item) {
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
						AgentStorage<TradableItem, Integer> storage = myTradingAgent.storage;
						storage.addItem(item);

						System.err.println(this.myAgent.getLocalName() + " Bought " + item.name() + ", now have" + storage);
						System.err.flush();
					}
				} else if (message.getPerformative() == ACLMessage.INFORM) {
					//nao tem items
				}
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

}
