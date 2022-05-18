package ggc.app.lookups;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

/**
 * Lookup products cheaper than a given price.
 */
public class DoLookupProductWithLeastTotalPrice extends Command<WarehouseManager> {

  public DoLookupProductWithLeastTotalPrice(WarehouseManager receiver) {
    super(Label.PRODUCTS_WITH_LEAST_TOTAL_PRICE, receiver);
  }

  @Override
  public void execute() throws CommandException {
    _display.popup(_receiver.lookupProductWithLeastTotalPrice());
  }

}
