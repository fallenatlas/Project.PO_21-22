package ggc.exceptions;

public class PartnerNotFoundException extends Exception {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111645L;
    private String _id;

    public PartnerNotFoundException(String id) {
        super();
        _id = id;
    }

    public String getId() {
        return _id;
    }
}