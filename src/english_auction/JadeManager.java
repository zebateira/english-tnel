package english_auction;

import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import english_auction.agents.TradingAgent;

/**
 * Manager for the jade platform to create an English Auction
 * 
 */
public class JadeManager {

	public static final int						TIMEOUT	= 1000;

	/**
	 * Singleton instance.
	 */
	private static JadeManager instance;

	/**
	 * List of instantiated containers.
	 */
	final HashMap<String, ContainerController> containersHashMap;
	/**
	 * List of instantiated and started agents.
	 */
	final List<AgentController> agentList;

	private final Runtime jadeRuntime;

	/**
	 * Creates the Main Container in localhost on port 8888 and starts the Jade
	 * Platform.
	 */
	private JadeManager() {
		instance = this;

		this.agentList = new ArrayList<AgentController>();
		this.containersHashMap = new HashMap<String, ContainerController>();

		this.jadeRuntime = Runtime.instance();

		this.startJade();
	}

	/**
	 * Starts the Jade Platform.
	 * 
	 * if you want to add more setup logic do it here.
	 */
	private void startJade() {
		ProfileImpl pMain = new ProfileImpl("localhost", 8888, null);

		jadeRuntime.createMainContainer(pMain);
	}

	/**
	 * Returns the JadeManager singleton instance.
	 */
	public static JadeManager getInstance() {
		return instance == null ? new JadeManager() : instance;
	}

	/**
	 * Creates and starts a new container in the Jade platform.
	 * 
	 * @param containerName
	 *            the name of the container
	 */
	public void startNewContainer(String containerName) {
		ProfileImpl pContainer = new ProfileImpl(null, 8888, null);
		System.out.println("Launching container " + pContainer);

		ContainerController containerRef = this.jadeRuntime
				.createAgentContainer(pContainer);

		containersHashMap.put(containerName, containerRef);
	}

	/**
	 * Creates and starts a new agent in the specified container.
	 */
	public void startNewTradingAgent(String containerName, String agentName,
			Class<? extends TradingAgent> agentClass, Object[] objects) {

		ContainerController c = containersHashMap.get(containerName);

		try {
			AgentController ac = c.createNewAgent(agentName,
					agentClass.getName(), objects);
			agentList.add(ac);
			ac.start();

			System.out.println(agentName + " launched"); // TODO set on debug
														 // level
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	public AgentController getAgent(Agent requester, String wantedAgentName)
			throws ControllerException {

		return requester.getContainerController().getPlatformController()
				.getAgent(wantedAgentName);
	}

}
