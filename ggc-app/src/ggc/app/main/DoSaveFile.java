package ggc.app.main;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import java.io.IOException;
import ggc.WarehouseManager;
import ggc.exceptions.MissingFileAssociationException;

/**
 * Save current state to file under current name (if unnamed, query for name).
 */
class DoSaveFile extends Command<WarehouseManager> {

  /** @param receiver */
  DoSaveFile(WarehouseManager receiver) {
    super(Label.SAVE, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.save();
    } catch (MissingFileAssociationException e1) {
      try {
        _receiver.saveAs(Form.requestString(Prompt.newSaveAs()));
      } catch (IOException | MissingFileAssociationException e2) { e2.printStackTrace(); }
    } catch (IOException e) { e.printStackTrace(); }
  }

}
