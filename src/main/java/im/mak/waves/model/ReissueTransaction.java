package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class ReissueTransaction extends Transaction implements Chained {

    public static final int TYPE = 5;

    private final Base58 assetId;
    private final long quantity;
    private final boolean reissuable;
    private final byte chainId;

    public ReissueTransaction(Base58 assetId, long quantity, boolean reissuable, long fee, long timestamp, byte chainId) {
        this(assetId, quantity, reissuable, fee, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public ReissueTransaction(Base58 assetId, long quantity, boolean reissuable, long fee, long timestamp, byte chainId, Base58[] proofs) {
        this(assetId, quantity, reissuable, fee, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public ReissueTransaction(Base58 assetId, long quantity, boolean reissuable, long fee, long timestamp, byte chainId, Base58 id) {
        this(assetId, quantity, reissuable, fee, timestamp, chainId, new Base58[0], id);
    }

    public ReissueTransaction(Base58 assetId, long quantity, boolean reissuable, long fee, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.assetId = assetId;
        this.quantity = quantity;
        this.reissuable = reissuable;
        this.chainId = chainId;
    }

    public Base58 assetId() {
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
    public byte[] bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
