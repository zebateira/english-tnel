package english_auction.agents;

import english_auction.goods.Item;

/**
 * An Agent that sells {@link Item}s to a {@link BuyerAgent}.
 */
public interface SellerAgent {

	/**
	 * Returns the type of the {@link BuyerAgent} that the implementing agent
	 * will sell the {@link Item}s to.
	 */
	String getMyBuyerType();

}