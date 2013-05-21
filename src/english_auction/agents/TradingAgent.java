package english_auction.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Random;

import english_auction.goods.AgentStorage;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public abstract class TradingAgent extends Agent {

	public AgentStorage<TradableItem, Integer>	storage;

	@Override
	protected void setup() {
		super.setup();

		this.storage = new AgentStorage<TradableItem, Integer>();
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
	
	public boolean shouldBuy(TradableItem item, int value) {
		//TODO: esta cena
		return true;
	}

	public boolean shouldSell(TradableItem item, int value, int iterations) {
		//TODO: esta cena
		return true;
	}

	public int buyBid(TradableItem item, int sugestedBid, int iterations) {
		//TODO: esta cena
		return sugestedBid - new Random().nextInt(20);
	}

	public int startingBid(TradableItem item) {
		//TODO: esta cena
		return 100;
	}

	public boolean sellerOut(TradableItem item) {
		//TODO: esta cena
		return false;
	}

	public boolean buyerOut(TradableItem item) {
		//TODO: esta cena
		int objective = 3;

		return storage.get(item).equals(objective);
	}
}
