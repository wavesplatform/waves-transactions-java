package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class LeaseCancelTransaction extends Transaction implements Chained {

    public static final int TYPE = 9;

    private final Base58 leaseId;
    private final byte chainId;

    public LeaseCancelTransaction(Base58 leaseId, long fee, long timestamp, byte chainId) {
        this(leaseId, fee, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public LeaseCancelTransaction(Base58 leaseId, long fee, long timestamp, byte chainId, Base58[] proofs) {
        this(leaseId, fee, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public LeaseCancelTransaction(Base58 leaseId, long fee, long timestamp, byte chainId, Base58 id) {
        this(leaseId, fee, timestamp, chainId, new Base58[0], id);
    }

    public LeaseCancelTransaction(Base58 leaseId, long fee, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.leaseId = leaseId;
        this.chainId = chainId;
    }

    public Base58 leaseId() {
        return leaseId;
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
