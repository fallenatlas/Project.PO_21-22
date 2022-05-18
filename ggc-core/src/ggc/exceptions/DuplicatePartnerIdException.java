package ggc.exceptions;

public class DuplicatePartnerIdException extends Exception {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111641L;
    private String _id;

    public DuplicatePartnerIdException(String id) {
        super();
        _id = id;
    }

    public String getId() {
        return _id;
    }
}
