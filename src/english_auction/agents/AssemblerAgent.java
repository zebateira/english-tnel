package english_auction.agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import english_auction.behaviours.Buy;
import english_auction.behaviours.Sell;
import english_auction.goods.Item;


@SuppressWarnings("serial")
public class AssemblerAgent extends TradingAgent implements BuyerAgent, SellerAgent {

	public static final String TYPE = "assembler";

	// jade.core.Jade override methods

	@Override
	protected void setup() {
		super.setup();

		for (Object s : getArguments()) {
			Item item = Item.valueOf((String) s);
			this.storage.put(item, 0);
			addBehaviour(new Sell(item));
			addBehaviour(new Buy(item));
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
