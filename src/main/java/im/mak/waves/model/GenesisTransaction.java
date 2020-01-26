package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.account.Address;
import im.mak.waves.crypto.base.Base58;

//TODO base class
public class GenesisTransaction {

    public static final int TYPE = 1;

    //TODO static'n'constructors from/to bytes

    private Base58 id;
    private int type;
    private long fee;
    private long timestamp;
    private Base58 signature;
    private long amount;
    private Address recipient; //TODO AddressOrAlias? Recipient? Can not be alias?

    public GenesisTransaction(int type, Address recipient, long amount, long fee, long timestamp, Base58 signature) {
        this(type, recipient, amount, fee, timestamp, signature, new Base58(Bytes.empty()));
    }

    public GenesisTransaction(int type, Address recipient, long amount, long fee, long timestamp, Base58 signature, Base58 id) {
        this.id = id; //TODO copy?
        this.type = type;
        this.fee = fee;
        this.timestamp = timestamp;
        this.signature = signature; //TODO copy?
        this.amount = amount;
        this.recipient = recipient;
    }

    //TODO hashCode, equals, toString

    public Address recipient() {
        return recipient;
    }

    public long amount() {
        return amount;
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
