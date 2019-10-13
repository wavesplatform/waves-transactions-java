package com.wavesplatform.scheme.components;

import com.wavesplatform.crypto.Bytes;

public class Transfer {

    public static Transfer to(Bytes recipient, long amount) {
        return new Transfer(recipient, amount);
    }

    private final Bytes recipient;
    private final long amount;

    public Transfer(Bytes recipient, long amount) {
        this.recipient = recipient;
        this.amount = amount;
    }

    public Bytes recipient() {
        return recipient;
    }

    public long amount() {
        return amount;
    }
}
