package english_auction.goods;

import java.util.HashMap;

public class AgentStorage<T, Z> extends HashMap {

	public boolean hasAllDependencies(TradableItem item) {
		boolean acc = true;
		for (TradableItem dep : item.consumes)
			acc &= (int) this.get(dep) > 0;
		return acc;
	}

	public void addItem(TradableItem item) {
		this.put(item, (int) this.get(item) + 1);
	}

	public boolean removeItem(TradableItem item) {
		if (!hasAllDependencies(item))
			return false;
		for (TradableItem dep : item.consumes)
			this.put(dep, (int) this.get(dep) - 1);
		return true;
	}
}
