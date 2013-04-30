package sample_program;

import english_auction.JadeManager;
import english_auction.agents.AssemblerAgent;
import english_auction.agents.SupplierAgent;

public class Program {

	public static void main(String[] args) throws InterruptedException {

		JadeManager jadeManager = JadeManager.getInstance();

		jadeManager.startNewContainer("container0");

		//MOTHERBOARD, CPU, RAM, GRAPHIC, AUDIO, DRIVES
		jadeManager.startNewTradingAgent("container0", "supplier", SupplierAgent.class, new Object[] { "CPU-s" });

		jadeManager.startNewTradingAgent("container0", "assembler", AssemblerAgent.class, new Object[] { "CPU-b" });

		//jadeManager.startNewTradingAgent("container0", "client", ClientAgent.class, new Object[] { "COMPUTER-b" });
	}
}
