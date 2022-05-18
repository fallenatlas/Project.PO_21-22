package ggc.transactions;

import ggc.Partner;
import ggc.Product;

public class Acquisition extends Transaction {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111603L;

    public Acquisition (int id, Partner partner, Product product, int quantity, double pricePerItem, int date) {
        super(id, partner, product, quantity, pricePerItem, date);
    }

    @Override
    public String accept(TransactionVisitor v) {
        return v.visitAcquisition(this);
    }

    @Override
    public String toString() {
        return "COMPRA" + "|" + super.toString() + "|" + getDate();
    }
}
