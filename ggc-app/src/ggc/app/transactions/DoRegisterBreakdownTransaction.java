package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.*;
import ggc.app.exceptions.*;

/**
 * Register order.
 */
public class DoRegisterBreakdownTransaction extends Command<WarehouseManager> {

  public DoRegisterBreakdownTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_BREAKDOWN_TRANSACTION, receiver);
    addStringField("partnerKey", Prompt.partnerKey());  
    addStringField("productKey", Prompt.productKey());
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.registerBreakdownTransaction(stringField("partnerKey"), stringField("productKey"), integerField("amount"));
    }
    catch (ProductUnavailableException e){
      throw new UnavailableProductException(e.getKey(), e.getRequestedAmount(), e.getAvailableAmount());
    }
    catch (ProductNotFoundException e) {
      throw new UnknownProductKeyException(e.getKey());
    }
    catch (PartnerNotFoundException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
  }

}
