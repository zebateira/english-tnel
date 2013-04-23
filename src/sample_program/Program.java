package sample_program;

import english_auction.JadeManager;
import english_auction.agents.AssemblerAgent;
import english_auction.agents.ClientAgent;
import english_auction.agents.SupplierAgent;

public class Program {

	public static void main(String[] args) throws InterruptedException {

		JadeManager jadeManager = JadeManager.getInstance();

		jadeManager.startNewContainer("container0");

		//MOTHERBOARD, CPU, RAM, GRAPHIC, AUDIO, DRIVES
		jadeManager.startNewTradingAgent("container0", "supplier1", SupplierAgent.class, new Object[] { "CPU-s" });
		jadeManager.startNewTradingAgent("container0", "supplier2", SupplierAgent.class, new Object[] { "MOTHERBOARD-s" });
		jadeManager.startNewTradingAgent("container0", "supplier3", SupplierAgent.class, new Object[] { "GRAPHIC-s" });
		jadeManager.startNewTradingAgent("container0", "supplier4", SupplierAgent.class, new Object[] { "AUDIO-s" });
		jadeManager.startNewTradingAgent("container0", "supplier5", SupplierAgent.class, new Object[] { "DRIVES-s" });
		jadeManager.startNewTradingAgent("container0", "supplier6", SupplierAgent.class, new Object[] { "RAM-s" });

		jadeManager.startNewTradingAgent("container0", "assembler", AssemblerAgent.class, new Object[] { "CPU-b", "MOTHERBOARD-b", "GRAPHIC-b", "AUDIO-b", "DRIVES-b", "RAM-b", "COMPUTER-s" });

		jadeManager.startNewTradingAgent("container0", "client", ClientAgent.class, new Object[] { "COMPUTER-b" });
	}
}
