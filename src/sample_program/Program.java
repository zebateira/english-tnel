package sample_program;

import english_auction.JadeManager;
import english_auction.agents.AssemblerAgent;
import english_auction.agents.SupplierAgent;

public class Program {

	public static void main(String[] args) throws InterruptedException {
		
		JadeManager jadeManager = JadeManager.getInstance();

		jadeManager.startNewContainer("container0");

		//MOTHERBOARD, CPU, RAM, GRAPHIC, AUDIO, DRIVES
		jadeManager.startNewTradingAgent("container0", "supplier1", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "supplier2", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "supplier3", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "supplier4", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "supplier5", SupplierAgent.class, new Object[] { "CPU-s" });

		jadeManager.startNewTradingAgent("container0", "assembler1", AssemblerAgent.class, new Object[] { "CPU-b" });
		//		jadeManager.startNewTradingAgent("container0", "assembler2", AssemblerAgent.class, new Object[] { "CPU-b", "CPU-s" });
		//		jadeManager.startNewTradingAgent("container0", "assembler3", AssemblerAgent.class, new Object[] { "CPU-b", "CPU-s" });
		//
		//		jadeManager.startNewTradingAgent("container0", "client", ClientAgent.class, new Object[] { "CPU-b" });
	}
}
