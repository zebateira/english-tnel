import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agents.AssemblerAgent;
import agents.ClientAgent;
import agents.SupplierAgent;

public class Run {

	static Runtime rt;
	private static String hostname = "localhost";
	private static HashMap<String, ContainerController> containerList = new HashMap<String, ContainerController>();
	private static List<AgentController> agentList;

	public static void main(String[] args) throws InterruptedException {
		emptyPlatform();

		newContainer("container0");

		newAgent("container0", "suplier", SupplierAgent.class,
				new Object[] { "MOTHERBOARD" });
		newAgent("container0", "assembler", AssemblerAgent.class,
				new Object[] { "MOTHERBOARD" });
		newAgent("container0", "client", ClientAgent.class,
				new Object[] { "MOTHERBOARD" });

		newAgent("container0", "sup2", SupplierAgent.class,
				new Object[] { "MOTHERBOARD" });
		newAgent("container0", "sup3", SupplierAgent.class,
				new Object[] { "MOTHERBOARD" });
		newAgent("container0", "sup4", SupplierAgent.class,
				new Object[] { "CPU" });
		newAgent("container0", "sup5", SupplierAgent.class,
				new Object[] { "CPU" });
		newAgent("container0", "sup6", SupplierAgent.class,
				new Object[] { "CPU" });
		newAgent("container0", "sup7", SupplierAgent.class, new Object[] {
				"MOTHERBOARD", "CPU" });

		newAgent("container0", "ass1", AssemblerAgent.class, new Object[] {
				"MOTHERBOARD", "CPU" });
		newAgent("container0", "ass2", AssemblerAgent.class, new Object[] {
				"MOTHERBOARD", "CPU" });

	}

	private static void emptyPlatform() {
		rt = Runtime.instance();
		Profile pMain = new ProfileImpl(hostname, 8888, null);
		rt.createMainContainer(pMain);

		containerList = new HashMap<String, ContainerController>();
		agentList = new ArrayList<AgentController>();
	}

	private static void newContainer(String name) {
		ProfileImpl pContainer;
		ContainerController containerRef;

		pContainer = new ProfileImpl(null, 8888, null);
		System.out.println("Launching container " + pContainer);
		containerRef = rt.createAgentContainer(pContainer);
		containerList.put(name, containerRef);
	}

	private static void newAgent(String controller, String name,
			Class<?> agentClass, Object[] objtab) {
		ContainerController c = containerList.get(controller);
		try {
			AgentController ac = c.createNewAgent(name, agentClass.getName(),
					objtab);
			agentList.add(ac);
			ac.start();
			System.out.println(name + " launched");
		} catch (StaleProxyException e) {
		}
	}
}
