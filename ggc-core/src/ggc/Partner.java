package ggc;

import java.io.Serializable;
import java.text.Collator;
import java.util.Collection;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

import ggc.deliveries.DefaultDeliveryMethod;
import ggc.deliveries.DeliveryMethod;
import ggc.exceptions.TransactionNotFoundException;
import ggc.exceptions.SaleNotFoundException;
import ggc.notifications.Notification;
import ggc.transactions.*;
import ggc.states.*;

public class Partner implements Serializable, Comparable<Partner>, Observer {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111610L;
    private double _points = 0;
    private PartnerState _partnerState = new NormalPartnerState(this);

    public abstract class PartnerState implements Serializable {
        public PartnerState() {}

        public double getPoints() {
            return _points;
        }

        public void addPoints(double points) {
            _points += points;
        }

        public void setPoints(double points) {
            _points = points;
        }
        
        public abstract double amountToPay(double baseAmount, int daysToLimit, String period);
        public abstract double applyDiscount(double baseAmount, int daysToLimit, String period);
        public abstract double applyFine(double baseAmount, int daysToLimit, String period);

        public void processPoints(double amount, int daysToLimit) {
            if(daysToLimit <= 0) { increasePoints(amount); }
            else { decreasePoints(amount, daysToLimit); }
        }

        public abstract void increasePoints(double amount);
        public abstract void decreasePoints(double amount, int daysToLimit);
        public abstract String status();

        protected void setPartnerState(PartnerState partnerState) {
            _partnerState = partnerState;
        }

        protected Partner getPartner() {
            return Partner.this;
        }
    }

    private String _id;
    private String _name;
    private String _address;
    private TransactionManager _transactionManager = new TransactionManager();
    private List<Notification> _notifications = new ArrayList<>();
    private DeliveryMethod _deliveryMethod = new DefaultDeliveryMethod();

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getAddress() {
        return _address;
    }

    public double getPoints() {
        return _points;
    }

    public String getState() {
        return _partnerState.status();
    }

    public Partner(String id, String name, String address) {
        _id = id;
        _name = name;
        _address = address;
    }

    public void addNotification(Notification notification) {
        _notifications.add(notification);
    }

    public void update(Notification notification) {
        _deliveryMethod.deliver(notification, this);
    }

    public Transaction getTransaction(int id) throws TransactionNotFoundException {
        return _transactionManager.getTransaction(id);
    }

    public Sale getSale(int id) throws SaleNotFoundException {
        return _transactionManager.getSale(id);
    }

    public void addAcquisitionTransaction(Acquisition acquisition) {
        _transactionManager.addAcquisition(acquisition);
    }

    public Collection<Acquisition> getAcquisitionTransactions() {
        return _transactionManager.getAcquisitions();
    }

    public double getAcquisitionsValue() {
        return _transactionManager.getAcquisitionsValue();
    }

    public void addSaleTransaction(Sale sale) {
        _transactionManager.addSale(sale);
    }

    public void paidSale(Sale sale, int daysToLimit) {
        _transactionManager.paidSale(sale);
        processPoints(sale.getPaidAmount(), daysToLimit);
    }

    public double salesDebt(int date) {
        return _transactionManager.getSalesDebt(date);
    }

    public Collection<Transaction> getSalesAndBreakdowns() {
        return _transactionManager.getSalesAndBreakdowns();
    }

    public Collection<Sale> getPaidSaleTransactions() {
        return _transactionManager.getPaidSaleTransactions();
    }

    public double getSalesValue() {
        return _transactionManager.getSalesValue();
    }

    public void addBreakdownTransaction(Breakdown breakdown) {
        _transactionManager.addBreakdown(breakdown);
    }

    public int compareTo(Partner other) {
        return Collator.getInstance(Locale.getDefault()).compare(_id.toLowerCase(), other.getId().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Partner) {
            Partner other = (Partner) o;
            return _id.equals(other.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return _id + "|" + _name + "|" + _address + "|" + getState() + "|" + (int) Math.round(_points) + "|" +
        (int)  Math.round(_transactionManager.getAcquisitionsValue()) + "|" +
        (int)  Math.round(_transactionManager.getSalesValue()) + "|" +
        (int)  Math.round(_transactionManager.getPaidSalesValue()) + "|" +
        (int)  Math.round(_transactionManager.getAveragePaidSalesValue()) + "|" +
        (int)  Math.round(_transactionManager.getAverageTransactionValue());
    }

    public String calculateTimePeriod(int daysToLimit, Product product) {
        int n = product.getPeriodModifier();
        if(daysToLimit <= -n) { return "P1"; }
        else if(-n < daysToLimit && daysToLimit <= 0) { return "P2"; }
        else if(0 < daysToLimit && daysToLimit <= n) { return "P3"; }
        else { return "P4"; }
    }

    public double amountToPay(double baseAmount, int daysToLimit, Product product) {
        String period = calculateTimePeriod(daysToLimit, product);
        return _partnerState.amountToPay(baseAmount, daysToLimit, period);
    }

    public void processPoints(double amount, int daysToLimit) {
        _partnerState.processPoints(amount, daysToLimit);
    }

    public String showPartner() {
        StringBuilder str = new StringBuilder(toString());
        for(Notification notification : _notifications) {
            str.append("\n");
            str.append(notification.toString());
        }
        _notifications.clear();
        return str.toString();
    }
}