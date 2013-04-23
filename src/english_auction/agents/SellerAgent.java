package english_auction.agents;

import english_auction.goods.TradableItem;

/**
 * An Agent that sells {@link TradableItem}s to a {@link BuyerAgent}.
 */
public interface SellerAgent {

	/**
	 * Returns the type of the {@link BuyerAgent} that the implementing agent
	 * will sell the {@link TradableItem}s to.
	 */
	String getMyBuyerType();

}