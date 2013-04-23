package english_auction.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import english_auction.agents.TradingAgent;
import english_auction.goods.AgentStorage;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public class Sell extends Transaction {

	public Sell(TradableItem item) {
		super(item, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchInReplyTo(item.toString())));
	}

	@Override
	public void action() {
		try {
			Thread.sleep(5);
		}
		catch (InterruptedException e1) {
		}

		ACLMessage message = this.myAgent.receive(messageTemplate);

		if (message == null)
			return;

		synchronized (((TradingAgent) this.myAgent).storage) {
			AgentStorage<TradableItem, Integer> storage = ((TradingAgent) this.myAgent).storage;

			if (storage.hasAllDependencies(item)) {
				this.reply(message.getSender(), item.toString(), ACLMessage.PROPOSE, item.toString());

				storage.removeItem(item);
				System.out.println(this.myAgent.getLocalName() + " Sold " + item.name() + ", now have " + storage);
				System.out.flush();
			} else
				this.send(message.getSender(), "", ACLMessage.INFORM);
		}
	}

}
