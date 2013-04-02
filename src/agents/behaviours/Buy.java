package agents.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import agents.RS;
import agents.TradingAgent;
import agents.goods.Item;

@SuppressWarnings("serial")
public class Buy extends CyclicBehaviour {
	final TradingAgent	agent;
	String				sellerType;
	final Item			item;
	MessageTemplate		mt;

	public Buy(TradingAgent agent, String seller, Item item) {
		this.agent = agent;
		this.sellerType = seller;
		this.item = item;
		mt = MessageTemplate.MatchInReplyTo(item.toString());
	}

	@Override
	public void action() {
		try {
			DFAgentDescription[] sellers = RS.search(agent, sellerType);
			if (sellers.length > 0) {
				for (DFAgentDescription ag : sellers) {
					RS.send(agent, ag.getName(), "", ACLMessage.CFP, item.toString());
					ACLMessage message = agent.receive(mt);
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
