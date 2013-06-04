package english_auction.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Random;

import english_auction.JadeManager;
import english_auction.goods.AgentStorage;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public abstract class TradingAgent extends Agent {

	public AgentStorage<TradableItem, Integer>	storage;
	public int									gold;

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
		return gold >= value;
	}

	public boolean shouldSell(TradableItem item, int value) {
		return true;
	}

	public int buyBid(TradableItem item, int sugestedBid, int round) {
		if (round > 0)
			return sugestedBid - JadeManager.MIN_WAGE;
		return sugestedBid - new Random().nextInt(10);
	}

	public int startingBid(TradableItem item) {
		return item.value;
	}

	public boolean buyerOut() {
		return false;
	}

	public boolean sellerOut() {
		return false;
	}
}
