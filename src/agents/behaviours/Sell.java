package agents.behaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import agents.RS;
import agents.TradingAgent;

public class Sell extends SimpleBehaviour {
	TradingAgent agent;
	boolean sold = false;

	public Sell(TradingAgent agent) {
		this.agent = agent;
	}

	@Override
	public void action() {
		ACLMessage message = agent.receive();
		if (message != null && message.getContent().contains("buy") && agent.storage >= 1) {
			RS.send(agent, message.getSender(), "sell", 0);
			agent.storage++;
			sold = true;
		}
	}

	@Override
	public boolean done() {
		if (sold)
			System.out.println(agent.getLocalName() + " Sold");
		return sold;
	}

}
