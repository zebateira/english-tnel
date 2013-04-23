package english_auction.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

import english_auction.agents.TradingAgent;
import english_auction.goods.Item;

@SuppressWarnings("serial")
public class Sell extends Transaction {

	public Sell(Item item) {
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
			HashMap<Item, Integer> storage = ((TradingAgent) this.myAgent).storage;

			if (storage.get(item) > 0) {
				this.reply(message.getSender(), item.toString(), ACLMessage.PROPOSE, item.toString());

				storage.put(item, storage.get(item) - 1);
				System.out.println(this.myAgent.getLocalName() + " Sold " + item.name() + ", now have " + storage);
				System.out.flush();
			} else
				this.send(message.getSender(), "", ACLMessage.INFORM);
		}
	}

}
