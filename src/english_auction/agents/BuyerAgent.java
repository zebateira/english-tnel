package english_auction.agents;

import english_auction.goods.Item;

/**
 * An Agent that buys {@link Item}s from a {@link SellerAgent}.
 */
public interface BuyerAgent {

	/**
	 * Returns the type of the {@link SellerAgent} that the implementing agent
	 * will buy the {@link Item}s from.
	 */
	String getMySellerType();

}
