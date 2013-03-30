package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.wrapper.ControllerException;

import java.util.HashMap;

import agents.behaviours.Buy;
import agents.behaviours.Sell;
import agents.goods.Item;

@SuppressWarnings("serial")
public class AssemblerAgent extends TradingAgent {

	// método setup
	protected void setup() {
		// regista agente no DF
		try {
			RS.registerAgent(this, RS.assembler);
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
		storage.put(Item.MOTHERBOARD, 0);
		addBehaviour(new Sell(this, Item.MOTHERBOARD));
		addBehaviour(new Buy(this, RS.supplier, Item.MOTHERBOARD));
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Assembler out!\n");
		try {
			DFService.deregister(this);
		}
		catch (Exception e) {}
	}

}
