package sample_program;

import english_auction.JadeManager;
import english_auction.agents.AssemblerAgent;
import english_auction.agents.SupplierAgent;

public class Program {

	public static void main(String[] args) throws InterruptedException {
		
		JadeManager jadeManager = JadeManager.getInstance();

		jadeManager.startNewContainer("container0");

		jadeManager.startNewTradingAgent("container0", "random1", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "random2", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "random3", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "greedy", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "half", SupplierAgent.class, new Object[] { "CPU-s" });

		jadeManager.startNewTradingAgent("container0", "assembler1", AssemblerAgent.class, new Object[] { "CPU-b" });
	}
}
