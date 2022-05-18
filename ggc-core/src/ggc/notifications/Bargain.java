package ggc.notifications;

import ggc.Product;

public class Bargain extends Notification {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111021616L;

    public Bargain(Product product, double price) {
        super(product, price);
    }

    @Override
    public String toString() {
        return "BARGAIN|" + super.toString();
    }
}