package sample_program;

import english_auction.JadeManager;
import english_auction.agents.AssemblerAgent;
import english_auction.agents.ClientAgent;
import english_auction.agents.SupplierAgent;

public class Program {

	public static void main(String[] args) throws InterruptedException {

		JadeManager jadeManager = JadeManager.getInstance();

		jadeManager.startNewContainer("container0");

		jadeManager.startNewTradingAgent("container0", "supplier",
				SupplierAgent.class, new Object[] { "MOTHERBOARD" });
		jadeManager.startNewTradingAgent("container0", "assembler",
				AssemblerAgent.class, new Object[] { "MOTHERBOARD" });
		jadeManager.startNewTradingAgent("container0", "client",
				ClientAgent.class, new Object[] { "MOTHERBOARD" });

		jadeManager.startNewTradingAgent("container0", "sup2",
				SupplierAgent.class, new Object[] { "MOTHERBOARD" });
		jadeManager.startNewTradingAgent("container0", "sup3",
				SupplierAgent.class, new Object[] { "MOTHERBOARD" });
		jadeManager.startNewTradingAgent("container0", "sup4",
				SupplierAgent.class, new Object[] { "CPU" });
		jadeManager.startNewTradingAgent("container0", "sup5",
				SupplierAgent.class, new Object[] { "CPU" });
		jadeManager.startNewTradingAgent("container0", "sup6",
				SupplierAgent.class, new Object[] { "CPU" });

		jadeManager.startNewTradingAgent("container0", "ass1",
				AssemblerAgent.class, new Object[] { "MOTHERBOARD", "CPU" });
		jadeManager.startNewTradingAgent("container0", "ass2",
				AssemblerAgent.class, new Object[] { "MOTHERBOARD", "CPU" });

		jadeManager.startNewTradingAgent("container0", "cli2",
				ClientAgent.class, new Object[] { "CPU" });

	}

}
