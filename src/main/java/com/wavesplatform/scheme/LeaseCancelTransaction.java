package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class LeaseCancelTransaction extends Transaction implements Chained {

    public static final int TYPE = 9;

    private final Bytes leaseId;
    private final byte chainId;

    public LeaseCancelTransaction(Bytes leaseId, long fee, long timestamp, byte chainId) {
        this(leaseId, fee, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public LeaseCancelTransaction(Bytes leaseId, long fee, long timestamp, byte chainId, Bytes[] proofs) {
        this(leaseId, fee, timestamp, chainId, proofs, Bytes.empty());
    }

    public LeaseCancelTransaction(Bytes leaseId, long fee, long timestamp, byte chainId, Bytes id) {
        this(leaseId, fee, timestamp, chainId, new Bytes[0], id);
    }

    public LeaseCancelTransaction(Bytes leaseId, long fee, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.leaseId = leaseId;
        this.chainId = chainId;
    }

    public Bytes leaseId() {
        return leaseId;
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
