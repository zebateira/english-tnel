package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.wrapper.ControllerException;

import java.util.HashMap;

import agents.behaviours.Buy;
import agents.goods.Item;

@SuppressWarnings("serial")
public class ClientAgent extends TradingAgent {

	// método setup
	protected void setup() {
		// regista agente no DF
		try {
			RS.registerAgent(this, RS.client);
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
		for (Object s : getArguments()) {
			Item item = Item.valueOf((String) s);
			storage.put(item, 0);
			addBehaviour(new Buy(this, RS.assembler, item));
		}
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Buyer out!\n");
		try {
			DFService.deregister(this);
		}
		catch (Exception e) {}
	}

}
