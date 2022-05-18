package ggc.transactions;

import java.io.Serializable;
import ggc.Product;
import ggc.Partner;

public abstract class Transaction implements Serializable {
    private int _id;
    private int _date;
    private Partner _partner;
    private Product _product; 
    private int _quantity;
    private double _price;
    private double _pricePerItem;

    public int getId() {
        return _id;
    }

    public int getDate() {
        return _date;
    }

    public Partner getPartner() {
        return _partner;
    }

    public Product getProduct() {
        return _product;
    }

    public int getQuantity() {
        return _quantity;
    }

    public double getPricePerItem() {
        return _pricePerItem;
    }

    public double getValue() {
        return _quantity * _pricePerItem;
    }

    public double getPrice() {
        return _price;
    }

    public void setDate(int date) {
        _date = date;
    }

    public Transaction(int id, Partner partner, Product product, int quantity, double price) {
        _id = id;
        _partner = partner;
        _product = product;
        _quantity = quantity;
        _price = price;
    }

    public Transaction(int id, Partner partner, Product product, int quantity, double price, int date) {
        this(id, partner, product, quantity, price);
        _date = date;
    }

    public double totalValue() {
        double x = _quantity;
        double totalValue = x * _pricePerItem;
        return totalValue;
    }

    public String toString() {
        return _id + "|" + _partner.getId() + "|" + _product.getKey() + "|" + _quantity + "|" + (int) _price;
    }

    public abstract String accept(TransactionVisitor v);
}