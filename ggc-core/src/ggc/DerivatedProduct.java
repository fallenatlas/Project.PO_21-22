package ggc;

import java.util.Map;
import java.util.LinkedHashMap;
import ggc.exceptions.*;

/**
 * Class DerivatedProduct implements a derivated product.
 */
public class DerivatedProduct extends Product {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111616L;

    private int _periodModifier = 3;
    private double _alpha;
    private Map<Product, Integer> _recepy = new LinkedHashMap<>();

    /**
     * @param key of the product.
     * @param alpha of the recepy.
     * @param recepy
     */
    public DerivatedProduct(String key, double alpha, Map<Product, Integer> recepy) {
        super(key);
        _alpha = alpha;
        _recepy = recepy;
    }

    @Override
    /**
     * @return A Map that represents the recepy of the product.
     */
    public Map<Product, Integer> getRecepy() {
        return _recepy;
    }

    @Override
    public double getAlpha() {
        return _alpha;
    }

    @Override
    public int getPeriodModifier() {
        return _periodModifier;
    }

    @Override
    public void removeBatches(int quantity) throws NotEnoughBatchesException {
        try {
            super.removeBatches(quantity);
        } catch (NotEnoughBatchesException e) {
            int neededQuantity = quantity-getTotalStock();
            int producedQuantity = 0;
            while(producedQuantity < neededQuantity) {
                double price = 0;
                for(Product product : _recepy.keySet()) {
                    price += product.priceForRequestedAmount(_recepy.get(product));
                    product.removeBatches(_recepy.get(product));
                }
                updateHighestPrice(price*(1+_alpha));
                producedQuantity++;
            }
            removeAllBatches();
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(super.toString());
        str.append("|" + _alpha + "|");
        for(Product product : _recepy.keySet()) {
            str.append(product.getKey()).append(":");
            str.append(_recepy.get(product)).append("#");
        }
        return str.deleteCharAt(str.length()-1).toString();
    }

}
