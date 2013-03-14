package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.wrapper.ControllerException;
import agents.behaviours.Buy;
import agents.behaviours.Sell;

@SuppressWarnings("serial")
public class AssemblerAgent extends TradingAgent {

	// método setup
	protected void setup() {
		// regista agente no DF
		try {
			RS.registerAgent(this, RS.assembler);
		} catch (ControllerException e) {
			e.printStackTrace();
			return;
		} catch (FIPAException e) {
			e.printStackTrace();
			return;
		}

		addBehaviour(new Sell(this));
		addBehaviour(new Buy(this, RS.supplier));
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Assembler out!\n");
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}

}