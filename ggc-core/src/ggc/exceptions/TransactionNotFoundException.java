package ggc.exceptions;

public class TransactionNotFoundException extends Exception {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111650L;
    private int _id;

    public TransactionNotFoundException(int id) {
        super();
        _id = id;
    }

    public int getId() {
        return _id;
    }
}
