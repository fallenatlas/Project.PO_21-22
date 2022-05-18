package ggc.exceptions;

public class SaleNotFoundException extends Exception {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111649L;
    int _id;

    public SaleNotFoundException(int id) {
        super();
        _id = id;
    }

    public int getId() {
        return _id;
    }
}