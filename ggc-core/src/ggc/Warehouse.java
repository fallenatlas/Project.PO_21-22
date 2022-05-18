package ggc;
import java.io.Serializable;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import ggc.transactions.*;
import ggc.exceptions.*;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202110262313L;
  private int _date = 0;
  private int _numberOfTransactions = 0;
  private double _availableBalance = 0;
  private Map<String, Product> _products = new TreeMap<>(new CollatorWrapper());
  private Map<String, Partner> _partners = new TreeMap<>(new CollatorWrapper());

  /* ------------------- DATE ---------------------- */
  /**
   * @return _date attribute.
   */
  public int getDate() {
    return _date;
  }

  /**
   * @param days to advance.
   * @throws InvalidDaysToAdvanceException
   */
  public void advanceDate(int days) throws InvalidDaysToAdvanceException {
    if(days <= 0) {
      throw new InvalidDaysToAdvanceException(days);
    }
    _date += days;
  }

  /* ------------------- PRODUCT ---------------------- */
  /**
   * @param product to add.
   */
  public void addProduct(Product product) {
    _products.put(product.getKey().toLowerCase(), product);
  }

  /**
   * @param name of the product to add.
   */
  public void registerProduct(String name) {
    Product newProduct = new Product(name);
    addProduct(newProduct);
    newProduct.addObservers(Collections.unmodifiableCollection(_partners.values()));
  }

  public void registerProduct(String name, double alpha, String strRecepy) throws ProductNotFoundException {
    Map<Product, Integer> recepy = processRecipy(strRecepy);
    Product newProduct = new DerivatedProduct(name, alpha, recepy);
    addProduct(newProduct);
    newProduct.addObservers(Collections.unmodifiableCollection(_partners.values()));
  }

  /**
   * @param key of the product.
   * @return The product.
   */
  public Product getProduct(String key) throws ProductNotFoundException {
    Product product = _products.get(key.toLowerCase());
    if(product == null) { throw new ProductNotFoundException(key); }
    return product;
  }

  /**
   * @return A sorted Collection with all products.
   */
  public Collection<Product> showAllProducts() {
    return Collections.unmodifiableCollection(_products.values());
  }

  /* ------------------- PARTNER ---------------------- */
  /**
   * @param id of the partner.
   * @param name of the partner.
   * @param address of the partner.
   * @throws DuplicatePartnerIdException
   */
  public void registerPartner(String id, String name, String address) throws DuplicatePartnerIdException {
    if(_partners.get(id.toLowerCase()) != null){
      throw new DuplicatePartnerIdException(id);
    }
    _partners.put(id.toLowerCase(), new Partner(id,name,address));
  }

  /**
   * @param id of the partner.
   * @return The partner with the given id.
   * @throws PartnerNotFoundException
   */
  public Partner getPartner(String id) throws PartnerNotFoundException {
    Partner partner = _partners.get(id.toLowerCase());
    if(partner == null) { throw new PartnerNotFoundException(id); }
    return partner;
  }

  /**
   * @return A sorted Collection with all the partners.
   */
  public Collection<Partner> showAllPartners() {
    return Collections.unmodifiableCollection(_partners.values());
  }

  /**
   * @param id of the partner
   * @return A string with the partner and all its notifications.
   * @throws PartnerNotFoundException
   */
  public String showPartner(String id) throws PartnerNotFoundException {
    Partner partner = getPartner(id);
    return partner.showPartner();
  }

  public Collection<Acquisition> showPartnerAcquisitions(String partnerId) throws PartnerNotFoundException {
    Partner partner = getPartner(partnerId);
    return partner.getAcquisitionTransactions();
  }

  public String showPartnerSales(String partnerId) throws PartnerNotFoundException {
    Partner partner = getPartner(partnerId);
    StringBuilder str = new StringBuilder("");
    Collection<Transaction> salesAndBreakdowns = partner.getSalesAndBreakdowns();
    ShowTransaction showTransaction = new ShowTransaction(_date);
    for(Transaction transaction : salesAndBreakdowns) {
      str.append(transaction.accept(showTransaction));
      str.append("\n");
    }
    if(str.length() == 0) { return ""; }
    return str.deleteCharAt(str.length()-1).toString();
  }

  /* ------------------- BATCH ---------------------- */
  /**
   * @return A sorted Collection with all batches.
   */
  public Collection<Batch> showAvailableBatches() {
    List<Batch> batches = new ArrayList<>();
    for(Product product : _products.values()) {
      batches.addAll(product.getBatches());
    }
    return sortBatches(batches);
  }

  /**
   * @param key of the product.
   * @return A sorted Collection of batches that contain the product given by the key.
   */
  public Collection<Batch> showBatchesByProduct(String key) throws ProductNotFoundException {
    Product product = getProduct(key);
    return sortBatches(product.getBatches());
  }

  public Collection<Batch> showBatchesBypartner(String id) throws PartnerNotFoundException {
    Partner partner = getPartner(id);
    List<Batch> batches = new ArrayList<>();
    for(Product product : _products.values()) {
      batches.addAll(product.batchesByPartner(partner));
    }
    return sortBatches(batches);
  }

  public Collection<Batch> lookupProductBatchesUnderGivenPrice(double price) {
    List<Batch> batches = new ArrayList<>();
    for(Product product : _products.values()) {
      batches.addAll(product.batchesUnderGivenPrice(price));
    }
    return sortBatches(batches);
  }

  public Collection<Batch> sortBatches(List<Batch> batches) {
    Collections.sort(batches, (b1, b2) -> {
      if(!b1.getProduct().equals(b2.getProduct())) {
        return Batch.PRODUCT_COMPARATOR.compare(b1, b2);
      }
      else if(!b1.getProvider().equals(b2.getProvider())) {
        return Batch.PARTNER_COMPARATOR.compare(b1, b2);
      }
      else if(b1.getPrice() != b2.getPrice()) {
        return Batch.PRICE_COMPARATOR.compare(b1, b2);
      }
      else {
        return Batch.QUANTITY_COMPARATOR.compare(b1, b2);
      }
    });
    return Collections.unmodifiableCollection(batches);
  }

  /* ------------------- NOTIFICATION ---------------------- */
  public void toggleProductNotifications(String partnerId, String productKey) throws ProductNotFoundException, PartnerNotFoundException {
    Product product = getProduct(productKey);
    Partner partner = getPartner(partnerId);
    product.toggleNotifications(partner);
  }

  /* ------------------- TRANSACTION ---------------------- */
  public double showAvailableBalance() {
    return _availableBalance;
  }

  public double showAccountingBalance() {
    double accountingBalance = _availableBalance;
    for(Partner partner : _partners.values()) {
      accountingBalance += partner.salesDebt(_date);
    }
    return accountingBalance;
  }

  public void updateAvailableBalance(double amount) {
    _availableBalance += amount;
  }

  public void registerAcquisitionTransaction(String partnerId, String productKey, double price, int quantity) throws ProductNotFoundException, PartnerNotFoundException {
    Product product = getProduct(productKey);
    Partner partner = getPartner(partnerId);
    double totalPrice = executeAcquisition(partner, product, price, quantity);
    Acquisition newAcquisition = new Acquisition(_numberOfTransactions++, partner, product, quantity, totalPrice, _date);
    partner.addAcquisitionTransaction(newAcquisition);
    updateAvailableBalance(-(price * quantity));
  }

  public double executeAcquisition(Partner partner, Product product, double price, int quantity) throws ProductNotFoundException, PartnerNotFoundException {
    Batch newBatch = new Batch(product, partner, quantity, price);
    product.addAcquisitionedBatch(newBatch);
    return price*quantity;
  }

  public double executeAcquisition(Partner partner, Product product, int quantity) throws ProductNotFoundException, PartnerNotFoundException {
    if(product.getTotalStock() == 0) {
      return executeAcquisition(partner, product, product.getHighestPrice(), quantity);
    }
    return executeAcquisition(partner, product, product.getLowestCurrentPrice(), quantity);
  }

  public void registerAcquisitionTransactionWithNewProduct(String partnerId, String productKey, double price, int amount) throws ProductNotFoundException, PartnerNotFoundException {
    registerProduct(productKey);
    registerAcquisitionTransaction(partnerId, productKey, price, amount);
  }

  public void registerAcquisitionTransactionWithNewProduct(String partnerId, String productKey, double alpha, String strRecepy, double price, int amount) throws ProductNotFoundException, PartnerNotFoundException {
    registerProduct(productKey, alpha, strRecepy);
    registerAcquisitionTransaction(partnerId, productKey, price, amount);
  }

  public void registerSaleTransaction(String partnerId, int limitDate, String productKey, int quantity) throws ProductUnavailableException, PartnerNotFoundException, ProductNotFoundException {
    Product product = getProduct(productKey);
    Partner partner = getPartner(partnerId);
    try {
    double price = executeSale(product, quantity);
    Sale newSale = new Sale(_numberOfTransactions++, partner, product, quantity, price, limitDate);
    partner.addSaleTransaction(newSale);
    } catch (NotEnoughBatchesException e) { e.printStackTrace(); }
  }

  public double executeSale(Product product, int quantity) throws ProductUnavailableException, NotEnoughBatchesException {
    try {
      double price = checkQuantityAndGetPriceForSale(product, quantity);
      product.removeBatches(quantity); 
      return price;
    } catch (ProductNotProducibleException e) { 
      throw new ProductUnavailableException(e.getKey(), e.getRequestedAmount(), e.getAvailableAmount()); }
  }

  public double checkQuantityAndGetPriceForSale(Product product, int quantity) throws ProductNotProducibleException {
    double salePrice;
    try { 
      salePrice = product.priceForRequestedAmount(quantity); 
    } 
    catch (NotEnoughBatchesException e1) {
      int neededQuantity = quantity-product.getTotalStock();
      try {
        Map<Product, Integer> recepy = product.getRecepy();
        double alpha = product.getAlpha();
        salePrice = product.priceForTotalStock();
        double productPrice = 0;
        for(Product p : recepy.keySet()) {
          productPrice += checkQuantityAndGetPriceForSale(p, recepy.get(p)*neededQuantity);
        }
        salePrice += productPrice*(1+alpha);
      } 
      catch (OperationUnsupportedException e2) {
        throw new ProductNotProducibleException(product.getKey(), quantity, product.getTotalStock()); 
      }
    }
    return salePrice;
  }

  public Sale getSale(int id) throws TransactionNotFoundException, SaleNotFoundException {
    for(Partner partner : _partners.values()) {
      try {
        return partner.getSale(id);
      } catch (SaleNotFoundException e) {}
    }
    if(id < _numberOfTransactions && id >= 0) { throw new SaleNotFoundException(id); }
    throw new TransactionNotFoundException(id);
  }

  public Transaction getTransaction(int id) throws TransactionNotFoundException {
    for(Partner partner : _partners.values()) {
      try {
        return partner.getTransaction(id);
      } catch (TransactionNotFoundException e) {}
    }
    throw new TransactionNotFoundException(id);
  }

  public double amountToPay(Sale sale) {
    Partner partner = sale.getPartner();
    int daysToLimit = _date - sale.getLimitDate();
    double amountToPay = partner.amountToPay(sale.getPrice(), daysToLimit, sale.getProduct());
    return amountToPay;
  }

  public void receivePayment(int transactionKey) throws TransactionNotFoundException {
    try {
      Sale sale = getSale(transactionKey);
      double amountToPay = amountToPay(sale);
      sale.receivePayment(amountToPay, _date);
      sale.getPartner().paidSale(sale, _date-sale.getLimitDate());
      updateAvailableBalance(amountToPay);
    } 
    catch (SaleNotFoundException e) {}
    catch (SaleAlreadyPaidException e) {}
  }

  public String showTransaction(int id) throws TransactionNotFoundException {
    Transaction transaction = getTransaction(id);
    ShowTransaction showTransaction = new ShowTransaction(_date);
    return transaction.accept(showTransaction);
  }

  public String lookupPaymentsByPartner(String partnerId) throws PartnerNotFoundException {
    Partner partner = getPartner(partnerId);
    StringBuilder paidTransactions = new StringBuilder();
    ShowTransaction showTransaction = new ShowTransaction(_date);
    for(Sale sale : partner.getPaidSaleTransactions()) {
      paidTransactions.append(/*showSale(sale))*/sale.accept(showTransaction)).append("\n");
    }
    if(paidTransactions.length() > 0) {
      paidTransactions.deleteCharAt(paidTransactions.length()-1);
    }
    return paidTransactions.toString();
  }

  public void registerBreakdownTransaction(String partnerId, String productKey, int quantity) throws ProductUnavailableException, PartnerNotFoundException, ProductNotFoundException {
    Product product = getProduct(productKey);
    Partner partner = getPartner(partnerId);
    try {
      double derivatedProductsValue = product.priceForRequestedAmount(quantity);
      Map<Product, Double> acquiredProducts = executeBreakdown(partner, product, quantity);
      double price = calculateBreakdownPrice(derivatedProductsValue, acquiredProducts);
      double paidPrice = calculatePaidBreakdownPrice(price);
      Breakdown newBreakdown = new Breakdown(_numberOfTransactions++, partner, product, quantity, price, paidPrice, acquiredProducts, _date);
      partner.addBreakdownTransaction(newBreakdown);
      product.removeBatches(quantity);
      if(paidPrice > 0) { partner.processPoints(paidPrice, 0); }
      if(price < 0) { updateAvailableBalance(-price); }
    } catch (NotEnoughBatchesException e) {
      throw new ProductUnavailableException(product.getKey(), quantity, product.getTotalStock());
    } catch (OperationUnsupportedException e) {}
  }

  public Map<Product, Double> executeBreakdown(Partner partner, Product product, int quantity) throws ProductUnavailableException, OperationUnsupportedException, PartnerNotFoundException, ProductNotFoundException {
    return acquireProductElements(partner, product, quantity);
  }

  public Map<Product, Double> acquireProductElements(Partner partner, Product product, int quantity) throws OperationUnsupportedException, PartnerNotFoundException, ProductNotFoundException {
    Map<Product, Integer> recepy = product.getRecepy();
    Map<Product, Double> acquiredProducts = new LinkedHashMap<>();
    for(Product p : recepy.keySet()) {
      acquiredProducts.put(p, executeAcquisition(partner, p, quantity*recepy.get(p)));
    }
    return acquiredProducts;
  }

  public double calculateBreakdownPrice(double derivatedProductsValue, Map<Product, Double> acquiredProducts) {
    double price = derivatedProductsValue;
    double acquiredProductsPrice = 0;
    for(Double d : acquiredProducts.values()) {
      acquiredProductsPrice += d;
    }
    return price-acquiredProductsPrice;
  }

  public double calculatePaidBreakdownPrice(double price) {
    if(price <= 0) { return 0; }
    return price;
  }

  /* =================== Teste Pratico =================== */

  public Product lookupProductWithLeastAmoutOfStock() {
    List<Product> products = new ArrayList<>(_products.values());
    Product minStockProduct = products.get(0);
    int minStock = minStockProduct.getTotalStock();
    for(Product p : products) {
      if(p.getTotalStock() < minStock) {
        minStockProduct = p;
        minStock = p.getTotalStock();
      }
    }
    return minStockProduct;
  }

  public Partner lookupPartnerWithLeastAcquisitionsValue() {
    List<Partner> partners = new ArrayList<>(_partners.values());
    Partner minPurchaseValuePartner = partners.get(0);
    double minPurchaseValue = minPurchaseValuePartner.getAcquisitionsValue();
    for(Partner partner : partners) {
      if(partner.getAcquisitionsValue() < minPurchaseValue) {
        minPurchaseValue = partner.getAcquisitionsValue();
        minPurchaseValuePartner = partner;
      }
    }
    return minPurchaseValuePartner;
  }

  public Product lookupProductWithLeastTotalPrice() {
    List<Product> products = new ArrayList<>(_products.values());
    Product product = products.get(0);
    double minTotalPrice = product.priceForTotalStock();
    for(Product p : products) {
      if(p.priceForTotalStock() < minTotalPrice) {
        product = p;
        minTotalPrice = p.priceForTotalStock();
      }
    }
    return product;
  }

  public Partner lookupPartnerWithMostSalesValue() {
    List<Partner> partners = new ArrayList<>(_partners.values());
    Partner partner = partners.get(0);
    double maxSalesValue = partner.getSalesValue();
    for(Partner p : partners) {
      if(p.getSalesValue() > maxSalesValue) {
        partner = p;
        maxSalesValue = partner.getSalesValue();
      }
    }
    return partner;
  }

  /**
   * @param txtfile filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   */
  void importFile(String txtfile) throws IOException, BadEntryException,
                                  DuplicatePartnerIdException, PartnerNotFoundException,
                                  ProductNotFoundException 
    {
    try (BufferedReader reader = new BufferedReader(new FileReader(txtfile))) {
      String line;
      while((line = reader.readLine()) != null) {
        String[] fields = line.split("\\|");
        registerFromFields(fields);
      }
    }
  }

  /**
   * @param fields
   */
  void registerFromFields(String[] fields) throws BadEntryException, DuplicatePartnerIdException,
                                           PartnerNotFoundException, ProductNotFoundException 
    {
    switch(fields[0]) {
      case "PARTNER" -> registerPartner(fields);
      case "BATCH_S", "BATCH_M" -> registerBatch(fields);
      default -> throw new BadEntryException(fields[0]);
    }
  }

  /**
   * @param fields with the details of the partner.
   */
  void registerPartner(String[] fields) throws DuplicatePartnerIdException {
    registerPartner(fields[1], fields[2], fields[3]);
  }

  /**
   * @param fields with the details of the batch.
   */
  void registerBatch(String[] fields) throws PartnerNotFoundException, ProductNotFoundException {
    String productName = fields[1];
    String key = productName.toLowerCase();
    Partner partner = getPartner(fields[2]);
    double price = Double.parseDouble(fields[3]);
    int quantity = Integer.parseInt(fields[4]);

    if(fields[0].equals("BATCH_S")) {
      if(_products.get(key) == null) {
        registerProduct(productName);
      }
      Product product = _products.get(key);
      Batch batch = new Batch(product, partner, quantity, price);
      product.addBatch(batch);
    }
    else if(fields[0].equals("BATCH_M")) {
      double alpha = Double.parseDouble(fields[5]);
      if(_products.get(key) == null) {
        registerProduct(productName, alpha, fields[6]);
      }
      Product product = _products.get(key);
      Batch batch = new Batch(product, partner, quantity, price);
      product.addBatch(batch);
    }
  }

  /**
   * @param strRecepy
   * @return A Map that represents the recepy given by strRecepy.
   */
  public Map<Product, Integer> processRecipy(String strRecepy) throws ProductNotFoundException {
    Map<Product, Integer> recepy = new LinkedHashMap<>();
    String[] pairs = strRecepy.split("\\#");
    for(String pair : pairs) {
      String[] productAndQuantity = pair.split("\\:");
      Product product = getProduct(productAndQuantity[0]);
      recepy.put(product, Integer.parseInt(productAndQuantity[1]));
    }
    return recepy;
  }
}
