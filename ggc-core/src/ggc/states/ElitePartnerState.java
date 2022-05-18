package ggc.states;

import ggc.Partner;

public class ElitePartnerState extends Partner.PartnerState {
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202111111601L;

    public ElitePartnerState(Partner partner) {
        partner.super();
    }

    @Override
    public double amountToPay(double baseAmount, int daysToLimit, String period) {
        switch(period) {
            case "P1", "P2", "P3": 
                return applyDiscount(baseAmount, daysToLimit, period);
            default:
                return baseAmount;
        }
    }

    public double applyDiscount(double baseAmount, int daysToLimit, String period) {
        double amount = baseAmount;
        if(period.equals("P1") || period.equals("P2")) { amount = amount * 0.9; }
        else if(period.equals("P3")) { amount = amount * 0.95; }
        return amount;
    }

    public double applyFine(double baseAmount, int daysToLimit, String period) {
        return baseAmount;
    }

    @Override
    public void increasePoints(double amount) {
        addPoints(amount * 10);
    }

    @Override
    public void decreasePoints(double amount, int daysToLimit) {
        if(daysToLimit > 15) { 
            setPoints(getPoints() * 0.25);
            setPartnerState(new SelectionPartnerState(getPartner()));
        }
    }

    @Override
    public String status() {
        return "ELITE";
    }
}
