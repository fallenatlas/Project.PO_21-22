package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.*;
import ggc.exceptions.*;

/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("partnerKey", Prompt.partnerKey());
    addIntegerField("limitDate", Prompt.paymentDeadline());
    addStringField("productKey", Prompt.productKey());
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.registerSaleTransaction(stringField("partnerKey"),integerField("limitDate"), stringField("productKey"), integerField("amount"));
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
