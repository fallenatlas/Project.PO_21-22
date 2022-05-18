package ggc.app.lookups;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

/**
 * Lookup products cheaper than a given price.
 */
public class DoLookupPartnerWithLeastAcquisitionsValue extends Command<WarehouseManager> {

  public DoLookupPartnerWithLeastAcquisitionsValue(WarehouseManager receiver) {
    super(Label.PRODUCTS_WITH_LEAST_ACQUISITIONS_VALUE, receiver);
  }

  @Override
  public void execute() throws CommandException {
    _display.popup(_receiver.lookupPartnerWithLeastAcquisitionsValue());
  }

}
