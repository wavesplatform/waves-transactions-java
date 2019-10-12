package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;

public class GenesisTransaction {

    private Bytes id;
    private int type;
    private long fee;
    private long timestamp;
    private Bytes signature;
    private long amount;
    private String recipient; //TODO Address? AddressOrAlias? Recipient? String? Bytes?

    public GenesisTransaction(int type, String recipient, long amount, long fee, long timestamp, Bytes signature) {
        this(Bytes.empty(), type, recipient, amount, fee, timestamp, signature);
    }

    public GenesisTransaction(Bytes id, int type, String recipient, long amount, long fee, long timestamp, Bytes signature) {
        this.id = id; //TODO copy?
        this.type = type;
        this.fee = fee;
        this.timestamp = timestamp;
        this.signature = signature; //TODO copy?
        this.amount = amount;
        this.recipient = recipient;
    }

    //TODO hashCode, equals, toString

    public long amount() {
        return amount;
    }

    public String recipient() {
        return recipient;
    }

    public int type() {
        return type;
    }

    public Bytes id() {
        return id; //TODO calc if empty
    }

    public long fee() {
        return fee;
    }

    public long timestamp() {
        return timestamp;
    }

    public Bytes signature() {
        return signature;
    }

    public Bytes bodyBytes() {
        return null; //TODO
    }


}
