package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Transaction;

public class SponsorFeeTransaction extends Transaction {

    public static final int TYPE = 14;

    private final Bytes assetId; //TODO type?
    private final long minSponsoredAssetFee;

    public SponsorFeeTransaction(Bytes assetId, long minSponsoredAssetFee, long fee, long timestamp) {
        this(assetId, minSponsoredAssetFee, fee, timestamp, new Bytes[0], Bytes.empty());
    }

    public SponsorFeeTransaction(Bytes assetId, long minSponsoredAssetFee, long fee, long timestamp, Bytes[] proofs) {
        this(assetId, minSponsoredAssetFee, fee, timestamp, proofs, Bytes.empty());
    }

    public SponsorFeeTransaction(Bytes assetId, long minSponsoredAssetFee, long fee, long timestamp, Bytes id) {
        this(assetId, minSponsoredAssetFee, fee, timestamp, new Bytes[0], id);
    }

    public SponsorFeeTransaction(Bytes assetId, long minSponsoredAssetFee, long fee, long timestamp, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.assetId = assetId;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
    }

    public Bytes assetId() {
        return assetId;
    }

    public long minSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    @Override
    public Bytes bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
