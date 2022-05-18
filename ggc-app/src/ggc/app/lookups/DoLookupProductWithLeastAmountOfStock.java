package ggc.app.lookups;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

/**
 * Lookup products cheaper than a given price.
 */
public class DoLookupProductWithLeastAmountOfStock extends Command<WarehouseManager> {

  public DoLookupProductWithLeastAmountOfStock(WarehouseManager receiver) {
    super(Label.PRODUCTS_WITH_LEAST_AMOUNT_OF_STOCK, receiver);
  }

  @Override
  public void execute() throws CommandException {
    _display.popup(_receiver.lookupProductWithLeastAmountOfStock());
  }

}
