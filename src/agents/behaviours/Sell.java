package agents.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import agents.RS;
import agents.TradingAgent;
import agents.goods.Item;

public class Sell extends CyclicBehaviour {
	final TradingAgent	agent;
	final Item			item;

	public Sell(TradingAgent agent, Item item) {
		this.agent = agent;
		this.item = item;
	}

	@Override
	public void action() {
		ACLMessage message = agent.receive();
		if (message != null && message.getContent().contains("buy")) {
			synchronized (agent.storage) {
				if (agent.storage.get(item) > 0) {
					RS.send(agent, message.getSender(), "sell", 0);
					agent.storage.put(item, agent.storage.get(item) - 1);
					System.out.println(agent.getLocalName() + " Sold, now have " + agent.storage);
					System.out.flush();
				}
			}
		}
	}

}
