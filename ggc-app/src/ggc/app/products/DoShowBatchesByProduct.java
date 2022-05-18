package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.ProductNotFoundException;
import ggc.app.exceptions.UnknownProductKeyException;

/**
 * Show all products.
 */
class DoShowBatchesByProduct extends Command<WarehouseManager> {

  DoShowBatchesByProduct(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_BY_PRODUCT, receiver);
    addStringField("key", Prompt.productKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _display.popup(_receiver.showBatchesByProduct(stringField("key")));
    }
    catch (ProductNotFoundException e) {
      throw new UnknownProductKeyException(e.getKey());
    }
  }

}
