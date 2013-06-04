package english_auction.agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import english_auction.behaviours.Sell;
import english_auction.goods.TradableItem;

@SuppressWarnings("serial")
public class SupplierAgent extends TradingAgent implements SellerAgent {

	public static final String TYPE = "supplier";

	// jade.core.Jade override methods

	@Override
	protected void setup() {
		super.setup();

		for (Object s : getArguments()) {
			String name = (String) s;
			TradableItem item = TradableItem.valueOf(name.replaceAll("-s", "").replaceAll("-b", ""));
			this.storage.put(item, 1);
			gold = 0;
			if (name.contains("-s"))
				addBehaviour(new Sell(this, item));
			//if (name.contains("-b"))
			//	  addBehaviour(new Buy(item));
		}
	}

	@Override
	protected void takeDown() {
		System.out.println(this.getLocalName() + ": " + "Supplier out!\n");
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	// Seller interface method

	@Override
	public String getMyBuyerType() {
		return AssemblerAgent.TYPE;
	}

	// TradingAgent override methods

	@Override
	public String getMyType() {
		return SupplierAgent.TYPE;
	}

}
