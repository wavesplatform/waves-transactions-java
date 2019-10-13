package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class ReissueTransaction extends Transaction implements Chained {

    public static final int TYPE = 5;

    private final Bytes assetId;
    private final long quantity;
    private final boolean reissuable;
    private final byte chainId;

    public ReissueTransaction(Bytes assetId, long quantity, boolean reissuable, long fee, long timestamp, byte chainId) {
        this(assetId, quantity, reissuable, fee, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public ReissueTransaction(Bytes assetId, long quantity, boolean reissuable, long fee, long timestamp, byte chainId, Bytes[] proofs) {
        this(assetId, quantity, reissuable, fee, timestamp, chainId, proofs, Bytes.empty());
    }

    public ReissueTransaction(Bytes assetId, long quantity, boolean reissuable, long fee, long timestamp, byte chainId, Bytes id) {
        this(assetId, quantity, reissuable, fee, timestamp, chainId, new Bytes[0], id);
    }

    public ReissueTransaction(Bytes assetId, long quantity, boolean reissuable, long fee, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.assetId = assetId;
        this.quantity = quantity;
        this.reissuable = reissuable;
        this.chainId = chainId;
    }

    public Bytes assetId() {
        return assetId;
    }

    public long quantity() {
        return quantity;
    }

    public boolean isReissuable() {
        return reissuable;
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
