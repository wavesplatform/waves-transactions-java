package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;

public class GenesisTransaction {

    private Base58 id;
    private int type;
    private long fee;
    private long timestamp;
    private Base58 signature;
    private long amount;
    private String recipient; //TODO Address? AddressOrAlias? Recipient? String? Bytes?

    public GenesisTransaction(int type, String recipient, long amount, long fee, long timestamp, Base58 signature) {
        this(new Base58(Bytes.empty()), type, recipient, amount, fee, timestamp, signature);
    }

    public GenesisTransaction(Base58 id, int type, String recipient, long amount, long fee, long timestamp, Base58 signature) {
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

    public Base58 id() {
        return id; //TODO calc if empty
    }

    public long fee() {
        return fee;
    }

    public long timestamp() {
        return timestamp;
    }

    public Base58 signature() {
        return signature;
    }

    public byte[] bodyBytes() {
        return null; //TODO
    }


}
