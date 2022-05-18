package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.WarehouseManager;
import ggc.exceptions.*;

/**
 * Show batches supplied by partner.
 */
class DoShowBatchesByPartner extends Command<WarehouseManager> {

  DoShowBatchesByPartner(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_SUPPLIED_BY_PARTNER, receiver);
    addStringField("id", Prompt.partnerKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _display.popup(_receiver.showBatchesByPartner(stringField("id")));
    }
    catch (PartnerNotFoundException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
  }
}
