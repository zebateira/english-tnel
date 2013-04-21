package english_auction.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.HashMap;

import english_auction.goods.Item;

@SuppressWarnings("serial")
public abstract class TradingAgent extends Agent {

	public HashMap<Item, Integer> storage;

	@Override
	protected void setup() {
		super.setup();

		this.storage = new HashMap<Item, Integer>();

		this.registerAgentOnDF();
	}

	private void registerAgentOnDF() {

		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setName(this.getName());
		serviceDescription.setType(this.getMyType());

		DFAgentDescription dfAgentDescription = new DFAgentDescription();
		dfAgentDescription.setName(this.getAID());
		dfAgentDescription.addServices(serviceDescription);

		try {
			DFService.register(this, dfAgentDescription);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	public String getAgentName() {
		return this.getLocalName();
	}

	public abstract String getMyType();
}
