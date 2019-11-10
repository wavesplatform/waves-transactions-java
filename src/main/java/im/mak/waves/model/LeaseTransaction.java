package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.account.Address;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class LeaseTransaction extends Transaction implements Chained {

    public static final int TYPE = 8;

    private final Address recipient;
    private final long amount;
    private final byte chainId;

    public LeaseTransaction(Address recipient, long amount, long fee, long timestamp, byte chainId) {
        this(recipient, amount, fee, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public LeaseTransaction(Address recipient, long amount, long fee, long timestamp, byte chainId, Base58[] proofs) {
        this(recipient, amount, fee, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public LeaseTransaction(Address recipient, long amount, long fee, long timestamp, byte chainId, Base58 id) {
        this(recipient, amount, fee, timestamp, chainId, new Base58[0], id);
    }

    public LeaseTransaction(Address recipient, long amount, long fee, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.recipient = recipient;
        this.amount = amount;
        this.chainId = chainId;
    }

    public Address recipient() {
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
    public byte[] bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
