package ggc.transactions;

public class ShowTransaction implements TransactionVisitor {
    private int _date;

    public ShowTransaction(int date) {
        _date = date;
    }

    public String visitAcquisition(Acquisition acquisition) {
        return acquisition.toString();
    }

    public String visitSale(Sale sale) {
        return sale.toString(_date);
    }

    public String visitBreakdown(Breakdown breakdown) {
        return breakdown.toString();
    }
}