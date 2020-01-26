package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class ExchangeTransaction extends Transaction implements Chained {

    public static final int TYPE = 7;

    private final Base58 assetId;
    private final long quantity;
    private final byte chainId;

    // TODO buyOrder/sellOrder, amount/price, buyMatcherFee/sellMatcherFee

    public ExchangeTransaction(Base58 assetId, long quantity, long fee, long timestamp, byte chainId) {
        this(assetId, quantity, fee, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public ExchangeTransaction(Base58 assetId, long quantity, long fee, long timestamp, byte chainId, Base58[] proofs) {
        this(assetId, quantity, fee, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public ExchangeTransaction(Base58 id, Base58 assetId, long quantity, long fee, long timestamp, byte chainId) {
        this(assetId, quantity, fee, timestamp, chainId, new Base58[0], id);
    }

    public ExchangeTransaction(Base58 assetId, long quantity, long fee, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.assetId = assetId;
        this.quantity = quantity;
        this.chainId = chainId;
    }

    public Base58 assetId() {
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
    public byte[] bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
