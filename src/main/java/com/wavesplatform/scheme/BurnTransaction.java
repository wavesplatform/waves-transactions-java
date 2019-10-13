package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class BurnTransaction extends Transaction implements Chained {

    public static final int TYPE = 6;

    private final Bytes assetId;
    private final long quantity;
    private final byte chainId;

    public BurnTransaction(Bytes assetId, long quantity, long fee, long timestamp, byte chainId) {
        this(assetId, quantity, fee, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public BurnTransaction(Bytes assetId, long quantity, long fee, long timestamp, byte chainId, Bytes[] proofs) {
        this(assetId, quantity, fee, timestamp, chainId, proofs, Bytes.empty());
    }

    public BurnTransaction(Bytes assetId, long quantity, long fee, long timestamp, byte chainId, Bytes id) {
        this(assetId, quantity, fee, timestamp, chainId, new Bytes[0], id);
    }

    public BurnTransaction(Bytes assetId, long quantity, long fee, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.assetId = assetId;
        this.quantity = quantity;
        this.chainId = chainId;
    }

    public Bytes assetId() {
        return assetId;
    }

    public long quantity() {
        return quantity;
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
