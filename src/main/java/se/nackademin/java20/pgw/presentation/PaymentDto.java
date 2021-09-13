package se.nackademin.java20.pgw.presentation;

import java.io.Serializable;

public class PaymentDto implements Serializable {
    private String reference;
    private long amount;

    public PaymentDto() {
    }

    public String getReference() {
        return reference;
    }

    public long getAmount() {
        return amount;
    }
}
