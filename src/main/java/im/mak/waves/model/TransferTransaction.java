package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.account.Address;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class TransferTransaction extends Transaction implements Chained {

    public static final int TYPE = 4;

    private final Address recipient; //TODO AddressOrAlias?
    private final long amount;
    private final Base58 assetId; //TODO type?
    private final String attachment; //TODO Bytes?
    private final byte chainId;

    public TransferTransaction(Address recipient, long amount, Base58 assetId, String attachment, long fee, Base58 feeAssetId, long timestamp, byte chainId) {
        this(recipient, amount, assetId, attachment, fee, feeAssetId, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public TransferTransaction(Address recipient, long amount, Base58 assetId, String attachment, long fee, Base58 feeAssetId, long timestamp, byte chainId, Base58[] proofs) {
        this(recipient, amount, assetId, attachment, fee, feeAssetId, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public TransferTransaction(Address recipient, long amount, Base58 assetId, String attachment, long fee, Base58 feeAssetId, long timestamp, byte chainId, Base58 id) {
        this(recipient, amount, assetId, attachment, fee, feeAssetId, timestamp, chainId, new Base58[0], id);
    }

    public TransferTransaction(Address recipient, long amount, Base58 assetId, String attachment, long fee, Base58 feeAssetId, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, feeAssetId, timestamp, proofs, id);
        this.recipient = recipient;
        this.amount = amount;
        this.assetId = assetId;
        this.attachment = attachment;
        this.chainId = chainId;
    }

    public Address recipient() {
        return recipient;
    }

    public long amount() {
        return amount;
    }

    public Base58 assetId() {
        return assetId;
    }

    public String attachment() {
        return attachment;
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
