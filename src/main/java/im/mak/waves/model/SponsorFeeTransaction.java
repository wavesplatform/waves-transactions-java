package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Transaction;

public class SponsorFeeTransaction extends Transaction {

    public static final int TYPE = 14;

    private final Base58 assetId; //TODO type?
    private final long minSponsoredAssetFee;

    public SponsorFeeTransaction(Base58 assetId, long minSponsoredAssetFee, long fee, long timestamp) {
        this(assetId, minSponsoredAssetFee, fee, timestamp, new Base58[0], new Base58(Bytes.empty()));
    }

    public SponsorFeeTransaction(Base58 assetId, long minSponsoredAssetFee, long fee, long timestamp, Base58[] proofs) {
        this(assetId, minSponsoredAssetFee, fee, timestamp, proofs, new Base58(Bytes.empty()));
    }

    public SponsorFeeTransaction(Base58 assetId, long minSponsoredAssetFee, long fee, long timestamp, Base58 id) {
        this(assetId, minSponsoredAssetFee, fee, timestamp, new Base58[0], id);
    }

    public SponsorFeeTransaction(Base58 assetId, long minSponsoredAssetFee, long fee, long timestamp, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.assetId = assetId;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
    }

    public Base58 assetId() {
        return assetId;
    }

    public long minSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    @Override
    public byte[] bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
