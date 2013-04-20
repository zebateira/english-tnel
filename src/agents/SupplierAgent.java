package agents;

import jade.domain.DFService;

import java.util.HashMap;

import agents.behaviours.Sell;
import agents.goods.Item;

@SuppressWarnings("serial")
public class SupplierAgent extends TradingAgent {

	// método setup
	protected void setup() {
		// regista agente no DF
		try {
			RS.registerAgent(this, RS.supplier);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		storage = new HashMap<Item, Integer>();

		for (Object s : getArguments()) {
			Item item = Item.valueOf((String) s);
			storage.put(item, 3);
			addBehaviour(new Sell(item));
		}
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Supplier out!\n");
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}

}
