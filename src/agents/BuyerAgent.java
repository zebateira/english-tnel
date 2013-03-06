package agents;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.wrapper.ControllerException;

@SuppressWarnings("serial")
public class BuyerAgent extends Agent {

	// método setup
	protected void setup() {
		// regista agente no DF
		try {
			RS.registerAgent(this, "Buyer");
		} catch (ControllerException e) {
			e.printStackTrace();
			return;
		} catch (FIPAException e) {
			e.printStackTrace();
			return;
		}

		addBehaviour(new SimpleBehaviour(this) {
			int n = 0;

			public void action() {
				System.out.println("I am a buyer, my name is " + myAgent.getLocalName() + " " + n);
				n++;
			}

			public boolean done() {
				return n >= 3;
			}
		});
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Buyer out!\n");
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}

}