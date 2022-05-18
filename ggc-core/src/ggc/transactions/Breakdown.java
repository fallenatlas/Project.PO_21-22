package ggc.transactions;

import java.util.Map;

import ggc.Partner;
import ggc.Product;
import ggc.exceptions.OperationUnsupportedException;

public class Breakdown extends Transaction {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111606L;
    Map<Product, Double> _acquiredProducts;
    private double _paidAmount;

    public double getPaidAmount() {
        return _paidAmount;
    }

    public Breakdown(int id, Partner partner, Product product, int quantity, double price, double paidAmount, Map<Product, Double> acquiredProducts, int date) {
        super(id, partner, product, quantity, price, date);
        _acquiredProducts = acquiredProducts;
        _paidAmount = paidAmount;
    }

    @Override
    public String accept(TransactionVisitor v) {
        return v.visitBreakdown(this);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("DESAGREGAÇÃO|" + super.toString() + "|" + (int) Math.round(_paidAmount) + "|" + getDate() + "|");
        try {
            Map<Product, Integer> recepy = getProduct().getRecepy();
            for(Product product : _acquiredProducts.keySet()) {
                str.append(product.getKey()+":").append(recepy.get(product)*getQuantity()+":").append((int) Math.round(_acquiredProducts.get(product))+"#");
            }
        } catch (OperationUnsupportedException e) { e.printStackTrace(); }
        return str.deleteCharAt(str.length()-1).toString();
    }
}
