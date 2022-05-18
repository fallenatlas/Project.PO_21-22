package ggc.transactions;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import ggc.exceptions.TransactionNotFoundException;
import ggc.exceptions.SaleNotFoundException;

public class TransactionManager implements Serializable {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111615L;
    private Map<Integer, Acquisition> _acquisitions = new TreeMap<>();
    private Map<Integer, Sale> _sales = new TreeMap<>();
    private Map<Integer, Breakdown> _breakdowns = new TreeMap<>();
    private double _acquisitionsValue=0;
    private double _salesValue=0;
    private double _paidSalesValue=0;

    public void addAcquisition(Acquisition transaction) {
        _acquisitions.put(transaction.getId(), transaction);
        _acquisitionsValue += transaction.getPrice();
    }

    public void addSale(Sale transaction) {
        _sales.put(transaction.getId(), transaction);
        _salesValue += transaction.getPrice();
    }

    public void addBreakdown(Breakdown transaction) {
        _breakdowns.put(transaction.getId(), transaction);
        //_paidSalesValue += transaction.getPaidAmount();
    }

    public Transaction getTransaction(int id) throws TransactionNotFoundException {
        Transaction transaction;
        if((transaction = _acquisitions.get(id)) != null) { return transaction; }
        else if((transaction = _sales.get(id)) != null) { return transaction; }
        else if((transaction = _breakdowns.get(id)) != null) { return transaction; }
        else {throw new TransactionNotFoundException(id); }
    }

    public Collection<Acquisition> getAcquisitions() {
        return Collections.unmodifiableCollection(_acquisitions.values());
    }

    public Sale getSale(int id) throws SaleNotFoundException {
        Sale sale = _sales.get(id);
        if(sale == null) { throw new SaleNotFoundException(id); }
        return sale;
    }

    public void paidSale(Sale sale) {
        _paidSalesValue += sale.getPaidAmount();
    }

    public double getSalesDebt(int date) {
        double amount = 0;
        for(Sale sale : _sales.values()) {
            if(!sale.isPaid()) {
                int daysToLimit = date-sale.getLimitDate();
                amount += sale.getPartner().amountToPay(sale.getPrice(), daysToLimit, sale.getProduct());
            }
        }
        return amount;
    }

    public Collection<Sale> getPaidSaleTransactions() {
        Map<Integer, Sale> paidSales = new TreeMap<>();
        for(Sale sale : _sales.values()) {
            if(sale.isPaid()) { paidSales.put(sale.getId(), sale); }
        }
        return Collections.unmodifiableCollection(paidSales.values());
    }

    public Collection<Transaction> getSalesAndBreakdowns() {
        Map<Integer, Transaction> salesAndBreakdowns = new TreeMap<>();
        salesAndBreakdowns.putAll(_sales);
        salesAndBreakdowns.putAll(_breakdowns);
        return Collections.unmodifiableCollection(salesAndBreakdowns.values());
    }

    public double getAcquisitionsValue() {
        return _acquisitionsValue;
    }

    public double getSalesValue() {
        return _salesValue;
    }

    public double getPaidSalesValue() {
        return _paidSalesValue;
    }

    public double getAveragePaidSalesValue() {
        int numberOfPaidTransactions = getPaidSaleTransactions().size();
        return _paidSalesValue/numberOfPaidTransactions;
    }

    public double getAverageTransactionValue() {
        double transactionsValue = _acquisitionsValue + _salesValue;
        double numberOfTransactions = _acquisitions.size() + _sales.size();
        return transactionsValue/numberOfTransactions;
    }
}
