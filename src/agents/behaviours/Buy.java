package agents.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import agents.RS;
import agents.TradingAgent;
import agents.goods.Item;

public class Buy extends CyclicBehaviour {
	final TradingAgent agent;
	String sellerType;
	final Item item;

	public Buy(TradingAgent agent, String seller, Item item) {
		this.agent = agent;
		this.sellerType = seller;
		this.item = item;
	}

	@Override
	public void action() {
		try {
			DFAgentDescription[] sellers = RS.search(agent, sellerType);
			if (sellers.length > 0) {
				RS.send(agent, sellers[0].getName(), "buy", 0);
				ACLMessage message = agent.receive();
				if (message != null && message.getContent().contains("sell")) {
					synchronized (agent) {
						agent.storage.put(item, agent.storage.get(item) + 1);
						System.out.println(agent.getLocalName() + " Bought, now have" + agent.storage);
					}
				}
			}
		} catch (FIPAException e) {
		}
	}
}
