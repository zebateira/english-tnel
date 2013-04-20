package agents.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

import agents.RS;
import agents.TradingAgent;
import agents.goods.Item;

@SuppressWarnings("serial")
public class Buy extends CyclicBehaviour {
	String sellerType;
	final Item item;
	MessageTemplate mt;

	public Buy(String seller, Item item) {
		this.sellerType = seller;
		this.item = item;
		mt = MessageTemplate.MatchInReplyTo(item.toString());
	}

	@Override
	public void action() {
		try {
			DFAgentDescription[] sellers = RS.search(this.myAgent, sellerType);

			if (sellers.length <= 0) return; // no one more to sell to

			for (DFAgentDescription ag : sellers) {
				RS.send(this.myAgent, ag.getName(), "", ACLMessage.CFP,
						item.toString());
				ACLMessage message = this.myAgent.receive(mt);

				if (message == null) return;

				if (message.getPerformative() == ACLMessage.PROPOSE) {
					synchronized (((TradingAgent) this.myAgent).storage) {
						HashMap<Item, Integer> storage = ((TradingAgent) this.myAgent).storage;
						storage.put(item, storage.get(item) + 1);
						System.err.println(this.myAgent.getLocalName()
								+ " Bought, now have" + storage);
						System.err.flush();
					}
				} else if (message.getPerformative() == ACLMessage.INFORM) {
					// não ha nada la...
				}
			}

		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
