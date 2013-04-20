package agents;

import jade.core.Agent;

import java.util.HashMap;

import agents.goods.Item;

@SuppressWarnings("serial")
public abstract class TradingAgent extends Agent {
	public HashMap<Item, Integer> storage;
}
