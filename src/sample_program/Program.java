package sample_program;

import english_auction.JadeManager;
import english_auction.agents.AssemblerAgent;
import english_auction.agents.ClientAgent;
import english_auction.agents.SupplierAgent;

public class Program {

	public static void main(String[] args) throws InterruptedException {

		JadeManager jadeManager = JadeManager.getInstance();

		jadeManager.startNewContainer("container0");

		jadeManager.startNewTradingAgent("container0", "supplier1", SupplierAgent.class, new Object[] { "CPU" });
		jadeManager.startNewTradingAgent("container0", "supplier2", SupplierAgent.class, new Object[] { "CPU" });
		jadeManager.startNewTradingAgent("container0", "supplier3", SupplierAgent.class, new Object[] { "MOTHERBOARD" });
		jadeManager.startNewTradingAgent("container0", "supplier4", SupplierAgent.class, new Object[] { "MOTHERBOARD" });
		jadeManager.startNewTradingAgent("container0", "supplier5", SupplierAgent.class, new Object[] { "MOTHERBOARD", "CPU" });

		jadeManager.startNewTradingAgent("container0", "assembler1", AssemblerAgent.class, new Object[] { "CPU" });
		jadeManager.startNewTradingAgent("container0", "assembler2", AssemblerAgent.class, new Object[] { "CPU" });
		jadeManager.startNewTradingAgent("container0", "assembler3", AssemblerAgent.class, new Object[] { "MOTHERBOARD" });
		jadeManager.startNewTradingAgent("container0", "assembler4", AssemblerAgent.class, new Object[] { "MOTHERBOARD" });
		jadeManager.startNewTradingAgent("container0", "assembler5", AssemblerAgent.class, new Object[] { "CPU", "MOTHERBOARD" });

		jadeManager.startNewTradingAgent("container0", "client", ClientAgent.class, new Object[] { "CPU", "MOTHERBOARD" });
	}
}
