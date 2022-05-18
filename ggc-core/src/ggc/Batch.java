package ggc;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Class Batch implements a batch.
 */
public class Batch implements Serializable {
    public final static Comparator<Batch> PRODUCT_COMPARATOR = new ProductComparator();
    public final static Comparator<Batch> PARTNER_COMPARATOR = new PartnerComparator();
    public final static Comparator<Batch> PRICE_COMPARATOR = new PriceComparator();
    public final static Comparator<Batch> QUANTITY_COMPARATOR = new QuantityComparator();

    /**
    * Class ProductComparator implements a comparator for batches by their product.
    */
    private static class ProductComparator implements Comparator<Batch> {
        @Override
        public int compare(Batch batch1, Batch batch2) {
            return batch1.getProduct().compareTo(batch2.getProduct());
        }
    }

    /**
    * Class PartnerComparator implements a comparator for batches by their partner.
    */
    private static class PartnerComparator implements Comparator<Batch> {
        @Override
        public int compare(Batch batch1, Batch batch2) {
            return batch1.getProvider().compareTo(batch2.getProvider());
        }
    }

    /**
    * Class PriceComparator implements a comparator for batches by their price.
    */
    private static class PriceComparator implements Comparator<Batch> {
        @Override
        public int compare(Batch batch1, Batch batch2) {
            return (int) (batch1.getPrice() - batch2.getPrice()); 
        }
    }

    /**
    * Class QuantityComparator implements a comparator for batches by their quantity.
    */
    private static class QuantityComparator implements Comparator<Batch> {
        @Override
        public int compare(Batch batch1, Batch batch2) {
            return batch1.getQuantity() - batch2.getQuantity();
        }
    }

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111609L;
    
    private Product _product;
    private Partner _provider;
    private int _quantity;
    private double _pricePerItem;

    /**
     * @param product to have.
     * @param provider that provides.
     * @param quantity of the product.
     * @param pricePerItem of the product.
     */
    public Batch(Product product, Partner provider, int quantity, double pricePerItem) {
        _product = product;
        _provider = provider;
        _quantity = quantity;
        _pricePerItem = pricePerItem;
    }

    /**
     * @return The product that the batch has.
     */
    public Product getProduct() {
        return _product;
    }
    
    /**
     * @return The partner that provided the batch.
     */
    public Partner getProvider() {
        return _provider;
    }
    
    /**
     * @return The quantity of product.
     */
    public int getQuantity() {
        return _quantity;
    }

    public void setQuantity(int quantity) {
        _quantity = quantity;
    }

    /**
     * @return The price of each item.
     */
    public double getPrice() {
        return _pricePerItem;
    }

    @Override
    public String toString() {
        return _product.getKey() + "|" + _provider.getId() + "|" + (int) Math.round(_pricePerItem) + "|" + _quantity;
    }
}
