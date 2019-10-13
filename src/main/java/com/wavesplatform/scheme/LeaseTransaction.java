package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class LeaseTransaction extends Transaction implements Chained {

    public static final int TYPE = 8;

    private final Bytes recipient;
    private final long amount;
    private final byte chainId;

    public LeaseTransaction(Bytes recipient, long amount, long fee, long timestamp, byte chainId) {
        this(recipient, amount, fee, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public LeaseTransaction(Bytes recipient, long amount, long fee, long timestamp, byte chainId, Bytes[] proofs) {
        this(recipient, amount, fee, timestamp, chainId, proofs, Bytes.empty());
    }

    public LeaseTransaction(Bytes recipient, long amount, long fee, long timestamp, byte chainId, Bytes id) {
        this(recipient, amount, fee, timestamp, chainId, new Bytes[0], id);
    }

    public LeaseTransaction(Bytes recipient, long amount, long fee, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.recipient = recipient;
        this.amount = amount;
        this.chainId = chainId;
    }

    public Bytes recipient() {
        return recipient;
    }

    public long amount() {
        return amount;
    }

    @Override
    public byte chainId() {
        return chainId;
    }

    @Override
    public Bytes bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
