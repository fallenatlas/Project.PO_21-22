package ggc.notifications;

import java.io.Serializable;

import ggc.Product;

public abstract class Notification implements Serializable {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111629L;
    private Product _product;
    private double _price;

    public Notification(Product product, double price) {
        _product = product;
        _price = price;
    }

    public Product getProduct() {
        return _product;
    }

    public double getPrice() {
        return _price;
    }

    public void setProduct(Product product) {
        _product = product;
    }

    public void setPrice(double price) {
        _price = price;
    }

    @Override
    public String toString() {
        return "" + _product.getKey() + "|" + (int) Math.round(_price);
    }
}