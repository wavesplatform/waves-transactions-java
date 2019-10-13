package com.wavesplatform.scheme.components;

public class Payment {

    private long amount;
    private String assetId;

    public Payment(long amount, String assetId) {
        this.amount = amount;
        this.assetId = assetId;
    }

    public long amount() {
        return amount;
    }

    public String assetId() {
        return assetId;
    }
}
