package ggc.transactions;

public interface TransactionVisitor {
    public String visitAcquisition(Acquisition transaction);
    public String visitSale(Sale transaction);
    public String visitBreakdown(Breakdown transaction);
}
