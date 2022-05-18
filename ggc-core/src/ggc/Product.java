package ggc;

import java.util.List;
import java.util.Locale;
import java.util.Collection;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import ggc.notifications.*;
import ggc.exceptions.*;



/**
 * Class Product implements a product.
 */
public class Product implements Comparable<Product>, Serializable, Subject {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111607L;

    private String _key;
    private double _highestPrice = Double.MIN_VALUE;
    private boolean _firstBatch = true;
    private int _periodModifier = 5;
    private List<Observer> _observers = new ArrayList<>();
    private List<Batch> _batches = new ArrayList<>();

    /**
     * @param key of the product.
     */
    public Product(String key) {
        _key = key;
    }

    public void setKey(String key) {
        _key = key;
    }

    /**
     * @return The key of the product.
     */
    public String getKey() {
        return _key;
    }

    /**
     * @param highestPrice registered for the product.
     */
    public void setHighestPrice(double highestPrice) {
        _highestPrice = highestPrice;
    }

    /**
     * @return The highest price registered for the product.
     */
    public double getHighestPrice() {
        return _highestPrice;
    }

    /**
     * @return A List with all the batches with the product.
     */
    public List<Batch> getBatches() {
        return _batches;
    }

    public int getPeriodModifier() {
        return _periodModifier;
    }

    public Map<Product, Integer> getRecepy() throws OperationUnsupportedException {
        throw new OperationUnsupportedException();
    }

    public double getAlpha() throws OperationUnsupportedException {
        throw new OperationUnsupportedException();
    }

    /**
     * @param batch to add.
     */
    public void addBatch(Batch batch) {
        _batches.add(batch);
        _firstBatch = false;
        updateHighestPrice(batch.getPrice());
    }

    public void addAcquisitionedBatch(Batch batch) {
        addingBatch(batch);
        addBatch(batch);
    }

    public void addingBatch(Batch batch) {
        if(!_firstBatch) {
            if(getTotalStock() == 0) {
                notifyObservers(new New(this, batch.getPrice()));
            }
            else if(batch.getPrice() < getLowestCurrentPrice()) {
                notifyObservers(new Bargain(this, batch.getPrice()));
            }
        }
    }

    public void notifyObservers(Notification notification) {
        for(Observer o : _observers) {
            o.update(notification);
        }
    }

    public void addObservers(Collection<Observer> observers) {
        for(Observer observer : observers) {
            _observers.add(observer);
        }
    }

    public void addObserver(Observer o) {
        _observers.add(o);
    }

    public void removeObserver(Observer o) {
        int i = _observers.indexOf(o);
        if(i >= 0 ) { _observers.remove(i); }
    }

    public boolean isObservedBy(Observer o) {
        return (_observers.contains(o));
    }

    public void toggleNotifications(Observer o) {
        if(isObservedBy(o)) { removeObserver(o); }
        else { addObserver(o); }
    }

    /**
     * @param price to update the highest price if it is bigger than the previous.
     */
    public void updateHighestPrice(double price) {
        if(price > _highestPrice || _highestPrice == Double.MIN_VALUE) { 
            _highestPrice = price; 
        }
    }

    
    /**
     * @return The lowest price within all the available batches.
     */
    public double getLowestCurrentPrice() {
        if(_batches == null) {}
        double min = _batches.get(0).getPrice();
        for(Batch batch : _batches) {
            if(batch.getPrice() < min) { min = batch.getPrice(); }
        }
        return min;
    }

    /**
     * @param price to compare to.
     * @return A List of all the batches with price under the given price.
     */
    public List<Batch> batchesUnderGivenPrice(double price) {
        List<Batch> batches = new ArrayList<>();
        for(Batch batch : _batches) {
            if(batch.getPrice() < price) { batches.add(batch); }
        }
        return batches;
    }

    public List<Batch> batchesByPartner(Partner partner) {
        List<Batch> batches = new ArrayList<>();
        for(Batch batch : _batches) {
            if(partner.compareTo(batch.getProvider()) == 0) { batches.add(batch); }
        }
        return batches;
    }
    
    /**
     * @return The total stock of the product considering all its batches.
     */
    public int getTotalStock() {
        int totalStock = 0;
        for(Batch batch : _batches) {
            totalStock += batch.getQuantity();
        }
        return totalStock;
    }

    public boolean hasQuantity(int quantity) {
        return getTotalStock() >= quantity;
    }

    public int getLowestPriceBatchIndex() throws NotEnoughBatchesException {
        int i = 0;
        double lowestPrice = _batches.get(i).getPrice();
        int index = 0;
        if(_batches.size() == 0) { throw new NotEnoughBatchesException(); }
        while(i < _batches.size()) {
            double price;
            if((price = _batches.get(i).getPrice()) < lowestPrice) {
                lowestPrice = price;
                index = i;
            }
            i++;
        }
        return index;
    }

    public double priceForRequestedAmount(int quantity) throws NotEnoughBatchesException {
        if(getTotalStock() < quantity) { throw new NotEnoughBatchesException(); }
        double price = 0;
        int remainingQuantity = quantity;
        int i = 0;
        List<Integer> batchesIndexes = batchesIndexesByPriceOrder();
        while(remainingQuantity > 0) {
            Batch batch = _batches.get(batchesIndexes.get(i));
            int batchQuantity = batch.getQuantity();
            if(batchQuantity >= remainingQuantity) {
                price += batch.getPrice()*remainingQuantity;
            }
            else {
                price += batch.getPrice()*batchQuantity;
            }
            remainingQuantity -= batchQuantity;
            i++;
        }
        return price;
    }

    public List<Integer> batchesIndexesByPriceOrder() {
        List<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < _batches.size(); i++) {
            indexes.add(i);
        }
        Collections.sort(indexes, (i1, i2) -> {
            return Batch.PRODUCT_COMPARATOR.compare(_batches.get(i1), _batches.get(i2));
        });
        return indexes;
    }

    public double priceForTotalStock() {
        double price = 0;
        for(Batch batch : _batches) {
            price += batch.getQuantity()*batch.getPrice();
        }
        return price;
    }

    public void removeAllBatches() {
        _batches.clear();
    }

    public void removeBatch(int index) {
        _batches.remove(index);
    }

    public void removeBatches(int quantity) throws NotEnoughBatchesException {
        if(getTotalStock() == quantity) { removeAllBatches(); }
        else if(getTotalStock() > quantity) {
            int remainingQuantity = quantity;
            while(remainingQuantity > 0) {
                int index = getLowestPriceBatchIndex();
                Batch batch = _batches.get(index);
                int batchQuantity = batch.getQuantity();
                if(batchQuantity > remainingQuantity) {
                    batch.setQuantity(batchQuantity-remainingQuantity);
                }
                else {
                    removeBatch(index);
                }
                remainingQuantity -= batchQuantity;
            }
        }
        else {
            throw new NotEnoughBatchesException();
        }
    }
    
    @Override
    public int compareTo(Product other) {
        return Collator.getInstance(Locale.getDefault()).compare(_key.toLowerCase(), other.getKey().toLowerCase());
    }

    @Override
    public String toString() {
        return _key + "|" + (int) Math.round(_highestPrice) + "|" + getTotalStock();
    }
}
