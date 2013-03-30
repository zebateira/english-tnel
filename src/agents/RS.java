package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public final class RS {

	public static String	supplier	= "suplier";
	public static String	assembler	= "assembler";
	public static String	client		= "client";
	public static String	itemSep		= "|";
	public static String	propSep		= ";";

	public static boolean	prints		= true;

	public static void send(Agent agent, AID receipt, String content, int type) {
		ACLMessage reply = new ACLMessage(type);
		reply.setContent(content);
		reply.addReceiver(receipt);
		agent.send(reply);
	}

	public static void send(Agent agent, AID receipt, String content, int type, String inReplyTo) {
		ACLMessage reply = new ACLMessage(type);
		reply.setContent(content);
		reply.addReceiver(receipt);
		reply.setInReplyTo(inReplyTo);
		agent.send(reply);
	}

	public static DFAgentDescription[] search(Agent agent, String filter) throws FIPAException {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType(filter);
		template.addServices(sd1);
		return DFService.search(agent, template);
	}

	public static AgentController getAgent(Agent requester, String wantedAgentsName) throws ControllerException {
		return requester.getContainerController().getPlatformController().getAgent(wantedAgentsName);
	}

	public static void registerAgent(Agent requester, String type) throws ControllerException, FIPAException {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(requester.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(requester.getName());
		sd.setType(type);
		dfd.addServices(sd);

		DFService.register(requester, dfd);
	}
}
