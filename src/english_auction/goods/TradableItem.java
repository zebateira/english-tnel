package english_auction.goods;

public enum TradableItem {
	MOTHERBOARD(60), CPU(100), RAM(50), GRAPHIC(80), DRIVES(40), AUDIO(20), COMPUTER(new TradableItem[] { MOTHERBOARD, CPU, RAM, GRAPHIC, AUDIO, DRIVES });

	public final int value;
	public final TradableItem[]	consumes;

	TradableItem(int value) {
		this.value = value;
		this.consumes = new TradableItem[] { this };
	}

	TradableItem(TradableItem[] dependencies) {
		this.consumes = dependencies;
		int total = 0;
		for (TradableItem i : this.consumes)
			total += i.value;
		this.value = total;
	}
}
