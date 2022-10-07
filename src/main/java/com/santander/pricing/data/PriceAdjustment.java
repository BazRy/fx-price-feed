package com.santander.pricing.data;

public enum PriceAdjustment {
    BID(-0.1),
    ASK(0.1);

    private double adjustmentPercent;

    PriceAdjustment(double adjustmentPercent) {
        this.adjustmentPercent = adjustmentPercent;
    }

    public double getAdjustmentPercent() {
        return adjustmentPercent;
    }
}
