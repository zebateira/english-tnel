package english_auction.agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import english_auction.behaviours.Buy;
import english_auction.behaviours.Sell;
import english_auction.goods.TradableItem;


@SuppressWarnings("serial")
public class AssemblerAgent extends TradingAgent implements BuyerAgent, SellerAgent {

	public static final String TYPE = "assembler";

	// jade.core.Jade override methods

	@Override
	protected void setup() {
		super.setup();

		for (Object s : getArguments()) {
			String name = (String) s;
			TradableItem item = TradableItem.valueOf(name.replaceAll("-s", "").replaceAll("-b", ""));
			this.storage.put(item, 0);
			gold = 1000;
			if (name.contains("-s"))
				addBehaviour(new Sell(this, item));
			if (name.contains("-b"))
				addBehaviour(new Buy(this, item));
		}
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Assembler out!\n");

		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}


	// Seller interface method

	@Override
	public String getMyBuyerType() {
		return ClientAgent.TYPE;
	}


	// Buyer Interface method

	@Override
	public String getMySellerType() {
		return SupplierAgent.TYPE;
	}


	// TradingAgent override methods

	@Override
	public String getMyType() {
		return AssemblerAgent.TYPE;
	}

}
