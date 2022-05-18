package ggc.app.transactions;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.PartnerNotFoundException;
import ggc.exceptions.ProductNotFoundException;
import ggc.app.exceptions.*;

/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("partnerKey", Prompt.partnerKey());
    addStringField("productKey", Prompt.productKey());
    addRealField("price", Prompt.price());
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.registerAcquisitionTransaction(stringField("partnerKey"), stringField("productKey"), realField("price"), integerField("amount"));
    }
    catch (PartnerNotFoundException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
    catch (ProductNotFoundException e) {
      try {
        if(Form.confirm(Prompt.addRecipe())) {
          String recepy = "";
          int n = Form.requestInteger(Prompt.numberOfComponents());
          double alpha = Form.requestReal(Prompt.alpha());
          for(int i = 0; i < n; i++) {
            recepy += Form.requestString(Prompt.productKey());
            recepy += ":" + Form.requestInteger(Prompt.amount());
            if(i != n-1) { recepy += "#"; }
          }
          _receiver.registerAcquisitionTransactionWithNewProduct(stringField("partnerKey"), stringField("productKey"), alpha, recepy, realField("price"), integerField("amount"));
        }
        else { 
          _receiver.registerAcquisitionTransactionWithNewProduct(stringField("partnerKey"), stringField("productKey"), realField("price"), integerField("amount"));
        }
      }
      catch (PartnerNotFoundException e2) {
        throw new UnknownPartnerKeyException(e2.getId());
      }
      catch (ProductNotFoundException e2) {
        throw new UnknownProductKeyException(e2.getKey());
      }
    }
  }

}
