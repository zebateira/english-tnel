package english_auction.agents;

import english_auction.goods.TradableItem;

/**
 * An Agent that buys {@link TradableItem}s from a {@link SellerAgent}.
 */
public interface BuyerAgent {

	/**
	 * Returns the type of the {@link SellerAgent} that the implementing agent
	 * will buy the {@link TradableItem}s from.
	 */
	String getMySellerType();

}
