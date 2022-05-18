package ggc.exceptions;

public class ProductNotFoundException extends Exception {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111646L;
    private String _key;

    public ProductNotFoundException(String key) {
        super();
        _key = key;
    }

    public String getKey() {
        return _key;
    }
}