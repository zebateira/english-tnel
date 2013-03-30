package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.wrapper.ControllerException;

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
		}
		catch (ControllerException e) {
			e.printStackTrace();
			return;
		}
		catch (FIPAException e) {
			e.printStackTrace();
			return;
		}

		storage = new HashMap<Item, Integer>();
		storage.put(Item.MOTHERBOARD, 3);

		addBehaviour(new Sell(this, Item.MOTHERBOARD));
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Supplier out!\n");
		try {
			DFService.deregister(this);
		}
		catch (Exception e) {}
	}

}
