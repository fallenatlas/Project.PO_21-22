package ggc.states;

import ggc.Partner;

public class NormalPartnerState extends Partner.PartnerState {
    public NormalPartnerState(Partner partner) {
        partner.super();
    }

    @Override
    public double amountToPay(double baseAmount, int daysToLimit, String period) {
        switch(period) {
            case "P1": 
                return applyDiscount(baseAmount, daysToLimit, period);
            case "P3", "P4":
                return applyFine(baseAmount, daysToLimit, period);
            default:
                return baseAmount;
        }
    }

    public double applyDiscount(double baseAmount, int daysToLimit, String period) {
        return baseAmount * 0.9;
    }

    public double applyFine(double baseAmount, int daysToLimit, String period) {
        double amount = baseAmount;
        if(period.equals("P3")) {
            amount += ((baseAmount * 0.05) * daysToLimit);
        }
        else if(period.equals("P4")) {
            amount += ((baseAmount * 0.1) * daysToLimit);
        }
        return amount;
    }

    @Override
    public void increasePoints(double amount) {
        addPoints(amount * 10);
        if(getPoints() > 25000) {
            setPartnerState(new ElitePartnerState(getPartner()));
        }
        else if(getPoints() > 2000) {
            setPartnerState(new SelectionPartnerState(getPartner()));
        }
    }

    @Override
    public void decreasePoints(double amount, int daysToLimit) {
        setPoints(0);
    }

    @Override
    public String status() {
        return "NORMAL";
    }
}
