package com.wavesplatform.scheme.common;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.crypto.Hash;
import com.wavesplatform.crypto.KeyPair;
import com.wavesplatform.crypto.Seed;

public abstract class Transaction {

    //TODO sender, senderPk, version, chainId?
    private int version; //TODO int or less? How to set latest?
    private Bytes id;
    private int type; //TODO int or less?
    private long fee;
    private Bytes feeAssetId;
    private long timestamp;
    private Bytes[] proofs;

    public Transaction(int type, long fee, Bytes feeAssetId, long timestamp) {
        this(type, fee, feeAssetId, timestamp, new Bytes[0], Bytes.empty());
    }

    public Transaction(int type, long fee, Bytes feeAssetId, long timestamp, Bytes[] proofs) {
        this(type, fee, feeAssetId, timestamp, proofs, Bytes.empty());
    }

    public Transaction(int type, long fee, Bytes feeAssetId, long timestamp, Bytes id) {
        this(type, fee, feeAssetId, timestamp, new Bytes[0], id);
    }

    public Transaction(int type, long fee, Bytes feeAssetId, long timestamp, Bytes[] proofs, Bytes id) {
        //TODO validate args
        this.id = id; //TODO copy?
        this.type = type;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.timestamp = timestamp;
        this.proofs = proofs; //TODO copy?
    }

    public Bytes id() {
        return id.isEmpty() ? Hash.blake(bodyBytes()) : id;
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
        //TODO add/update proofs
        return new KeyPair(privateKey).sign(bodyBytes());
    }

    public abstract Bytes bodyBytes(); //TODO implement in the all transactions

}
