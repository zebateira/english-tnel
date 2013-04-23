package english_auction.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import english_auction.agents.AssemblerAgent;
import english_auction.agents.ClientAgent;
import english_auction.agents.SupplierAgent;
import english_auction.goods.Item;

@SuppressWarnings("serial")
abstract class Transaction extends CyclicBehaviour {

	protected final Item item;
	protected final MessageTemplate messageTemplate;

	protected Transaction(Item item, MessageTemplate messageTemplate) {
		this.item = item;
		this.messageTemplate = messageTemplate;
	}

	@Override
	public abstract void action();

	/**
	 * Sends a message from the <i>senderAgent</i> to the <i>receiverAgent</i>
	 * with the <i>type</i> being one of the following:
	 * <ul>
	 * <li>{@link SupplierAgent#TYPE}</li>
	 * <li>{@link AssemblerAgent#TYPE}</li>
	 * <li>{@link ClientAgent#TYPE}</li>
	 * </ul>
	 * 
	 */
	protected final void send(AID receiverAgent, String msgContent, int type) {
		ACLMessage message = new ACLMessage(type);

		message.setContent(msgContent);
		message.addReceiver(receiverAgent);

		this.myAgent.send(message);
	}

	/**
	 * Same as {@link #send(Agent, AID, String, int) send}, plus the
	 * <i>inReplyTo</i> reference.
	 * 
	 */
	protected final void reply(AID receiverAgent, String msgContent, int type,
			String inReplyTo) {
		ACLMessage reply = new ACLMessage(type);

		reply.setContent(msgContent);
		reply.addReceiver(receiverAgent);
		reply.setInReplyTo(inReplyTo);

		this.myAgent.send(reply);
	}

	/**
	 * Searches for Agents given a type.
	 * 
	 * @return Array of Agents found that fit the criteria.
	 * @throws FIPAException
	 */
	protected DFAgentDescription[] search(String typeFilter)
			throws FIPAException {

		DFAgentDescription dfAgentDescription = new DFAgentDescription();
		ServiceDescription serviceDescription = new ServiceDescription();

		serviceDescription.setType(typeFilter);
		dfAgentDescription.addServices(serviceDescription);

		return DFService.search(this.myAgent, dfAgentDescription);
	}

}
