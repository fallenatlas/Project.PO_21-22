package ggc.notifications;

import ggc.Product;

public class New extends Notification {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111637L;

    public New(Product product, double price) {
        super(product, price);
    }

    @Override
    public String toString() {
        return "NEW|" + super.toString();
    }
}