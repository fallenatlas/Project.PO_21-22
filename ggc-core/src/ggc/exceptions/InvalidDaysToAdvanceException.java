package ggc.exceptions;

public class InvalidDaysToAdvanceException extends Exception {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111642L;
    private int _time;

    public InvalidDaysToAdvanceException(int time) {
        super();
        _time = time;
    }

    public int getTime() {
        return _time;
    }
}
