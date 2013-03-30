package agents;

import jade.core.Agent;

import java.util.HashMap;

import agents.goods.Item;

public abstract class TradingAgent extends Agent {
	public HashMap<Item, Integer> storage;
}
