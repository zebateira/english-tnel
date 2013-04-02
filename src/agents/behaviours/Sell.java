package agents.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import agents.RS;
import agents.TradingAgent;
import agents.goods.Item;

@SuppressWarnings("serial")
public class Sell extends CyclicBehaviour {
	final TradingAgent	agent;
	final Item			item;
	MessageTemplate		mt;

	public Sell(TradingAgent agent, Item item) {
		this.agent = agent;
		this.item = item;

		mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchInReplyTo(item.toString()));
	}

	@Override
	public void action() {
		//System.out.println(agent.getName() + " sell " + agent.getQueueSize());
		ACLMessage message = agent.receive(mt);
		if (message != null) {
			synchronized (agent.storage) {
				if (agent.storage.get(item) > 0) {
					RS.send(agent, message.getSender(), item.toString(), ACLMessage.PROPOSE, item.toString());

					agent.storage.put(item, agent.storage.get(item) - 1);
					System.out.println(agent.getLocalName() + " Sold, now have " + agent.storage);
					System.out.flush();
				}
				else
					RS.send(agent, message.getSender(), "", ACLMessage.INFORM);
			}
		}
	}

}
