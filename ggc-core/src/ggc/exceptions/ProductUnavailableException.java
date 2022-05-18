package ggc.exceptions;

public class ProductUnavailableException extends Exception {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111648L;
    private String _key;
    private int _requestedAmount;
    private int _availableAmount;

    public ProductUnavailableException(String key, int requestedAmount, int availableAmount) {
        super();
        _key = key;
        _requestedAmount = requestedAmount;
        _availableAmount = availableAmount;
    }

    public String getKey() {
        return _key;
    }

    public int getRequestedAmount() {
        return _requestedAmount;
    }

    public int getAvailableAmount() {
        return _availableAmount;
    }
}
