package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exceptions.*;
import ggc.WarehouseManager;
import ggc.exceptions.*;

/**
 * Toggle product-related notifications.
 */
class DoToggleProductNotifications extends Command<WarehouseManager> {

  DoToggleProductNotifications(WarehouseManager receiver) {
    super(Label.TOGGLE_PRODUCT_NOTIFICATIONS, receiver);
    addStringField("partnerId", Prompt.partnerKey());
    addStringField("productKey", Prompt.productKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _receiver.toggleProductNotifications(stringField("partnerId"), stringField("productKey"));
    } 
    catch (ProductNotFoundException e) { 
      throw new UnknownProductKeyException(e.getKey()); 
    } 
    catch (PartnerNotFoundException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
  }

}
