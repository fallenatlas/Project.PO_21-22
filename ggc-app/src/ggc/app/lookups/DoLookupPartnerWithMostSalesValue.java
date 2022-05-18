package ggc.app.lookups;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

/**
 * Lookup products cheaper than a given price.
 */
public class DoLookupPartnerWithMostSalesValue extends Command<WarehouseManager> {

  public DoLookupPartnerWithMostSalesValue(WarehouseManager receiver) {
    super(Label.PARTNER_WITH_MOST_SALES_VALUE, receiver);
  }

  @Override
  public void execute() throws CommandException {
    _display.popup(_receiver.lookupPartnerWithMostSalesValue());
  }

}
