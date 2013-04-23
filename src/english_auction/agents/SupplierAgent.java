package english_auction.agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import english_auction.behaviours.Sell;
import english_auction.goods.Item;

@SuppressWarnings("serial")
public class SupplierAgent extends TradingAgent implements SellerAgent {

	public static final String TYPE = "supplier";

	// jade.core.Jade override methods

	@Override
	protected void setup() {
		super.setup();

		for (Object s : this.getArguments()) {
			Item item = Item.valueOf((String) s);
			this.storage.put(item, 3);
			this.addBehaviour(new Sell(item));
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
