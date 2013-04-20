package agents.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

import agents.RS;
import agents.TradingAgent;
import agents.goods.Item;

@SuppressWarnings("serial")
public class Sell extends CyclicBehaviour {
	final Item item;
	MessageTemplate mt;

	public Sell(Item item) {
		this.item = item;

		mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.CFP),
				MessageTemplate.MatchInReplyTo(item.toString()));
	}

	@Override
	public void action() {
		// System.out.println(this.myAgent.getName() + " sell " +
		// this.myAgent.getQueueSize());
		ACLMessage message = this.myAgent.receive(mt);

		if (message == null) return;

		synchronized (((TradingAgent) this.myAgent).storage) {
			HashMap<Item, Integer> storage = ((TradingAgent) this.myAgent).storage;
			if (storage.get(item) > 0) {
				RS.send(this.myAgent, message.getSender(), item.toString(),
						ACLMessage.PROPOSE, item.toString());

				storage.put(item, storage.get(item) - 1);
				System.out.println(this.myAgent.getLocalName()
						+ " Sold, now have " + storage);
				System.out.flush();
			} else
				RS.send(this.myAgent, message.getSender(), "",
						ACLMessage.INFORM);
		}
	}

}
