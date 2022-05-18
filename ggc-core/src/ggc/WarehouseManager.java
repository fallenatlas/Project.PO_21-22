package ggc;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import ggc.exceptions.*;
import ggc.transactions.*;

//FIXME import classes (cannot import from pt.tecnico or ggc.app)

/** Façade for access. */
public class WarehouseManager {

  /** Name of file storing current store. */
  private String _filename = "";

  /** The warehouse itself. */
  private Warehouse _warehouse = new Warehouse();

  private boolean _changesSinceLastSave = false;

  public void warehouseStateChange() {
    if(_changesSinceLastSave == false) _changesSinceLastSave = true;
  }

  /* ------------------- DATE ---------------------- */
  /**
   * @param days to advance.
   * @throws InvalidDaysToAdvanceException
   */
  public void advanceDate(int days) throws InvalidDaysToAdvanceException {
    _warehouse.advanceDate(days);
    warehouseStateChange();
  }

  /**
   * @return int Current date
   */
  public int displayDate() {
    return _warehouse.getDate();
  }

  /* ------------------- PRODUCT ---------------------- */
  /**
   * @return A sorted Collection with all products.
   */
  public Collection<Product> showAllProducts() {
    return _warehouse.showAllProducts();
  }

  /* ------------------- BATCH ---------------------- */
  /**
   * @return A sorted Collection with all the batches.
   */
  public Collection<Batch> showAvailableBatches() {
    return _warehouse.showAvailableBatches();
  }

  /**
   * @param key of the product.
   * @return A sorted Collection of batches that contain the product given by the key.
   */
  public Collection<Batch> showBatchesByProduct(String key) throws ProductNotFoundException {
    return _warehouse.showBatchesByProduct(key);
  }

  public Collection<Batch> showBatchesByPartner(String id) throws PartnerNotFoundException {
    return _warehouse.showBatchesBypartner(id);
  }

  public Collection<Batch> lookupProductBatchesUnderGivenPrice(Double price) {
    return _warehouse.lookupProductBatchesUnderGivenPrice(price);
  }

  /* ------------------- PARTNER ---------------------- */
  public String showPartner(String id) throws PartnerNotFoundException {
    return _warehouse.showPartner(id);
  }

  public Collection<Partner> showAllPartners() {
    return _warehouse.showAllPartners();
  }

  public void registerPartner(String id, String name, String address) throws DuplicatePartnerIdException {
    _warehouse.registerPartner(id,name,address);
    warehouseStateChange();
  }

  public Collection<Acquisition> showPartnerAcquisitions(String partnerId) throws PartnerNotFoundException {
    return _warehouse.showPartnerAcquisitions(partnerId);
  }

  public String showPartnerSales(String partnerId) throws PartnerNotFoundException {
    return _warehouse.showPartnerSales(partnerId);
  }

  public String lookupPaymentsByPartner(String partnerId) throws PartnerNotFoundException {
    return _warehouse.lookupPaymentsByPartner(partnerId);
  }

  /* ------------------- NOTIFICATION ---------------------- */
  public void toggleProductNotifications(String partnerId, String productKey) throws ProductNotFoundException, PartnerNotFoundException{
    _warehouse.toggleProductNotifications(partnerId, productKey);
    warehouseStateChange();
  }

  public boolean hasSaveFile() {
    return _filename.equals("") ? false : true;
  }

  /* ------------------- TRANSACTION ---------------------- */
  public double showAvailableBalance() {
    return _warehouse.showAvailableBalance();
  }

  public double showAccountingBalance() {
    return _warehouse.showAccountingBalance();
  }

  public void registerAcquisitionTransaction(String partnerId, String productKey, double price, int quantity) throws ProductNotFoundException, PartnerNotFoundException {
    _warehouse.registerAcquisitionTransaction(partnerId, productKey, price, quantity);
    warehouseStateChange();
  }

  public void registerAcquisitionTransactionWithNewProduct(String partnerId, String productKey, double price, int amount) throws ProductNotFoundException, PartnerNotFoundException {
    _warehouse.registerAcquisitionTransactionWithNewProduct(partnerId, productKey, price, amount);
    warehouseStateChange();
  }

  public void registerAcquisitionTransactionWithNewProduct(String partnerId, String productKey, double alpha, String strRecepy, double price, int amount) throws ProductNotFoundException, PartnerNotFoundException {
    _warehouse.registerAcquisitionTransactionWithNewProduct(partnerId, productKey, alpha, strRecepy, price, amount);
    warehouseStateChange();
  }

  public void registerSaleTransaction(String partnerId, int limitDate, String productKey, int quantity) throws ProductUnavailableException, PartnerNotFoundException, ProductNotFoundException {
    _warehouse.registerSaleTransaction(partnerId, limitDate, productKey, quantity);
    warehouseStateChange();
  }

  public String showTransaction(int id) throws TransactionNotFoundException {
    return _warehouse.showTransaction(id);
  }

  public void registerBreakdownTransaction(String partnerId, String productKey, int quantity) throws ProductUnavailableException, PartnerNotFoundException, ProductNotFoundException {
    _warehouse.registerBreakdownTransaction(partnerId, productKey, quantity);
    warehouseStateChange();
  }

  public void receivePayment(int transactionKey) throws TransactionNotFoundException {
    _warehouse.receivePayment(transactionKey);
    warehouseStateChange();
  }

  /* =================== Teste Pratico =================== */

  public Product lookupProductWithLeastAmountOfStock() {
    return _warehouse.lookupProductWithLeastAmoutOfStock();
  }

  public Partner lookupPartnerWithLeastAcquisitionsValue() {
    return _warehouse.lookupPartnerWithLeastAcquisitionsValue();
  }

  public Product lookupProductWithLeastTotalPrice() {
    return _warehouse.lookupProductWithLeastTotalPrice();
  }

  public Partner lookupPartnerWithMostSalesValue() {
    return _warehouse.lookupPartnerWithMostSalesValue();
  }

  /**
   * @@throws IOException
   * @@throws FileNotFoundException
   * @@throws MissingFileAssociationException
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    if(_filename.equals("")) throw new MissingFileAssociationException();
    if(_changesSinceLastSave) {
      try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)))) {
        out.writeObject(_warehouse);
      }
    }
  }

  /**
   * @@param filename
   * @@throws MissingFileAssociationException
   * @@throws IOException
   * @@throws FileNotFoundException
   */
  public void saveAs(String filename) throws MissingFileAssociationException, FileNotFoundException, IOException {
    _filename = filename;
    save();
  }

  /**
   * @@param filename
   * @@throws UnavailableFileException
   */
  public void load(String filename) throws UnavailableFileException {
    try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
      _warehouse = (Warehouse) in.readObject();
      _filename = filename;
      _changesSinceLastSave = false;
    }
    catch (IOException | ClassNotFoundException e) { throw new UnavailableFileException(filename); }
  }

  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
	    _warehouse.importFile(textfile);
      _changesSinceLastSave = true;
    } catch (IOException | BadEntryException | DuplicatePartnerIdException | ProductNotFoundException | PartnerNotFoundException e) {
	    throw new ImportFileException(textfile);
    }
  }
}
