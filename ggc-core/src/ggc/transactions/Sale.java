package ggc.transactions;

import ggc.Partner;
import ggc.Product;
import ggc.exceptions.SaleAlreadyPaidException;

public class Sale extends Transaction {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111559L;
    private boolean _paid = false;
    private double _paidAmount;
    private int _limitDate;

    public int getLimitDate() {
        return _limitDate;
    }

    public double getPaidAmount() {
        return _paidAmount;
    }

    public boolean isPaid() {
        return _paid;
    }

    public void setLimitDate(int limitDate) {
        _limitDate = limitDate;
    }

    public void receivePayment(double paidAmount, int paymentDate) throws SaleAlreadyPaidException {
        if(_paid == false) {
            _paidAmount = paidAmount;
            setDate(paymentDate);
            _paid = true;
        }
        else {
            throw new SaleAlreadyPaidException();
        }
    }

    public Sale (int id, Partner partner, Product product, int quantity, double price, int limitDate) {
        super(id, partner, product, quantity, price);
        _limitDate = limitDate;
    }


    public String toString(int date) {
        if(_paid) { return toString(); }
        String str = "VENDA|" + super.toString();
        double amountToPay = getPartner().amountToPay(getPrice(), date-_limitDate, getProduct());
        str += "|" + (int) Math.round(amountToPay) + "|" + _limitDate;
        return str;
    }

    public String accept(TransactionVisitor v) {
        return v.visitSale(this);
    }

    @Override
    public String toString() {
        return "VENDA|" + super.toString() + "|" + (int) Math.round(_paidAmount) + "|" + _limitDate + "|" + getDate();
    }
}