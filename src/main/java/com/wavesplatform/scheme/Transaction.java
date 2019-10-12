package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.crypto.KeyPair;
import com.wavesplatform.crypto.Seed;

public abstract class Transaction {

    private int version; //TODO int or less? How to set latest?
    private Bytes id;
    private int type; //TODO int or less?
    private long fee;
    private Bytes feeAssetId;
    private long timestamp;
    private Bytes[] proofs;

    public Transaction(int type, long fee, Bytes feeAssetId, long timestamp, Bytes[] proofs) {
        this(Bytes.of(new byte[0]), type, fee, feeAssetId, timestamp, proofs);
    }

    public Transaction(Bytes id, int type, long fee, Bytes feeAssetId, long timestamp, Bytes[] proofs) {
        this.id = id; //TODO copy?
        this.type = type;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.timestamp = timestamp;
        this.proofs = proofs; //TODO copy?
    }

    public Bytes id() {
        return id;
    }

    public int type() {
        return type;
    }

    public long fee() {
        return fee;
    }

    public Bytes feeAssetId() {
        return feeAssetId;
    }

    public long timestamp() {
        return timestamp;
    }

    public Bytes[] proofs() {
        return proofs;
    }

    public Bytes sign(Seed seed) {
        return sign(seed.keys().privateKey());
    }

    public Bytes sign(Bytes privateKey) {
        return new KeyPair(privateKey).sign(bodyBytes());
    }

    public abstract Bytes bodyBytes();

}
