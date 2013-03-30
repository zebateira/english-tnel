package agents.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import agents.RS;
import agents.TradingAgent;
import agents.goods.Item;

public class Buy extends CyclicBehaviour {
	final TradingAgent	agent;
	String				sellerType;
	final Item			item;

	public Buy(TradingAgent agent, String seller, Item item) {
		this.agent = agent;
		this.sellerType = seller;
		this.item = item;
	}

	@Override
	public void action() {
		//System.out.println(agent.getName() + " buy " + agent.getQueueSize());
		try {
			DFAgentDescription[] sellers = RS.search(agent, sellerType);
			if (sellers.length > 0) {
				for (DFAgentDescription ag : sellers) {
					RS.send(agent, ag.getName(), "", ACLMessage.CFP, item.toString());
					ACLMessage message = agent.receive(MessageTemplate.MatchInReplyTo(item.toString()));
					if (message != null) {
						if (message.getPerformative() == ACLMessage.PROPOSE) {
							synchronized (agent.storage) {
								agent.storage.put(item, agent.storage.get(item) + 1);
								System.err.println(agent.getLocalName() + " Bought, now have" + agent.storage);
								System.err.flush();
							}
						}
						else if (message.getPerformative() == ACLMessage.INFORM) {
							// não há nada lá...
						}
					}
				}
			}
		}
		catch (FIPAException e) {}
	}
}
