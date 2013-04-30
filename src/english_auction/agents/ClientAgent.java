package english_auction.agents;

import jade.domain.DFService;
import english_auction.behaviours.Buy;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public class ClientAgent extends TradingAgent implements BuyerAgent {

	public static final String TYPE = "client";

	// jade.core.Agent override methods

	@Override
	protected void setup() {
		super.setup();

		for (Object s : getArguments()) {
			String name = (String) s;
			TradableItem item = TradableItem.valueOf(name.replaceAll("-s", "").replaceAll("-b", ""));
			this.storage.put(item, 0);
			//if (name.contains("-s"))
			//	addBehaviour(new Sell(item));
			if (name.contains("-b"))
				addBehaviour(new Buy(this, item));
		}
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Buyer out!\n");
		try {
			DFService.deregister(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Buyer interface method

	@Override
	public String getMySellerType() {
		return AssemblerAgent.TYPE;
	}

	// TradingAgent override methods

	@Override
	public String getMyType() {
		return ClientAgent.TYPE;
	}

}
