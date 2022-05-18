package ggc.states;

import ggc.Partner;

public class SelectionPartnerState extends Partner.PartnerState {
    public SelectionPartnerState(Partner partner) {
        partner.super();
    }

    @Override
    public double amountToPay(double baseAmount, int daysToLimit, String period) {
        switch(period) {
            case "P1", "P2": 
                return applyDiscount(baseAmount, daysToLimit, period);
            case "P3", "P4":
                return applyFine(baseAmount, daysToLimit, period);
            default:
                return baseAmount;
        }
    }

    public double applyDiscount(double baseAmount, int daysToLimit, String period) {
        double amount = baseAmount;
        if(period.equals("P1")) { amount = amount * 0.9; }
        else if(period.equals("P2") && daysToLimit <= -2) { amount = amount * 0.95; }
        return amount;
    }

    public double applyFine(double baseAmount, int daysToLimit, String period) {
        double amount = baseAmount;
        if(period.equals("P3") && daysToLimit > 1) {
            amount += ((baseAmount * 0.02) * daysToLimit);
        }
        else if(period.equals("P4")) {
            amount += ((baseAmount * 0.05) * daysToLimit);
        }
        return amount;
    }
    
    @Override
    public void increasePoints(double amount) {
        addPoints(amount * 10);
        if(getPoints() > 25000) {
            setPartnerState(new ElitePartnerState(getPartner()));
        }
    }

    @Override
    public void decreasePoints(double amount, int daysToLimit) {
        if(daysToLimit > 2) { 
            setPoints(getPoints() * 0.1);
            setPartnerState(new NormalPartnerState(getPartner()));
        }
    }

    @Override
    public String status() {
        return "SELECTION";
    }
}
