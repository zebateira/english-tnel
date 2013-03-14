package agents.behaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import agents.RS;
import agents.TradingAgent;

public class Buy extends SimpleBehaviour {
	TradingAgent agent;
	String seller;
	boolean bought = false;

	public Buy(TradingAgent agent, String seller) {
		this.agent = agent;
		this.seller = seller;
	}

	@Override
	public void action() {
		try {
			DFAgentDescription[] assemblers = RS.search(agent, seller);
			if (assemblers.length > 0) {
				RS.send(agent, assemblers[0].getName(), "buy", 0);
				ACLMessage message = agent.receive();
				if (message != null && message.getContent().contains("sell")) {
					agent.storage++;
					bought = true;
				}
			}
		} catch (FIPAException e) {
		}
	}

	@Override
	public boolean done() {
		if (bought)
			System.out.println(agent.getLocalName() + " Bought");
		return bought;
	}

}
