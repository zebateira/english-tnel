package english_auction.goods;

public enum Item {
	MOTHERBOARD(60), CPU(100), RAM(50);
	public final int value;

	Item(int value) {
		this.value = value;
	}
}
