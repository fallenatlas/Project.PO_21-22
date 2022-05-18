package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.TransactionNotFoundException;
import ggc.app.exceptions.UnknownTransactionKeyException;

/**
 * Show specific transaction.
 */
public class DoShowTransaction extends Command<WarehouseManager> {

  public DoShowTransaction(WarehouseManager receiver) {
    super(Label.SHOW_TRANSACTION, receiver);
    addIntegerField("id", Prompt.transactionKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
    _display.popup(_receiver.showTransaction(integerField("id")));
    }
    catch (TransactionNotFoundException e) {
      throw new UnknownTransactionKeyException(e.getId());
    }
  }

}
