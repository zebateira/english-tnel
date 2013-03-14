package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.wrapper.ControllerException;
import agents.behaviours.Sell;

@SuppressWarnings("serial")
public class SupplierAgent extends TradingAgent {

	// método setup
	protected void setup() {
		// regista agente no DF
		try {
			RS.registerAgent(this, RS.supplier);
		} catch (ControllerException e) {
			e.printStackTrace();
			return;
		} catch (FIPAException e) {
			e.printStackTrace();
			return;
		}

		storage = 1;

		addBehaviour(new Sell(this));
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
