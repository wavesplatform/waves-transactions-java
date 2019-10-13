package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class TransferTransaction extends Transaction implements Chained {

    public static final int TYPE = 4;

    private final Bytes recipient; //TODO AddressOrAlias?
    private final long amount;
    private final Bytes assetId; //TODO type?
    private final String attachment; //TODO Bytes?
    private final byte chainId;

    public TransferTransaction(Bytes recipient, long amount, Bytes assetId, String attachment, long fee, Bytes feeAssetId, long timestamp, byte chainId) {
        this(recipient, amount, assetId, attachment, fee, feeAssetId, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public TransferTransaction(Bytes recipient, long amount, Bytes assetId, String attachment, long fee, Bytes feeAssetId, long timestamp, byte chainId, Bytes[] proofs) {
        this(recipient, amount, assetId, attachment, fee, feeAssetId, timestamp, chainId, proofs, Bytes.empty());
    }

    public TransferTransaction(Bytes recipient, long amount, Bytes assetId, String attachment, long fee, Bytes feeAssetId, long timestamp, byte chainId, Bytes id) {
        this(recipient, amount, assetId, attachment, fee, feeAssetId, timestamp, chainId, new Bytes[0], id);
    }

    public TransferTransaction(Bytes recipient, long amount, Bytes assetId, String attachment, long fee, Bytes feeAssetId, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, feeAssetId, timestamp, proofs, id);
        this.recipient = recipient;
        this.amount = amount;
        this.assetId = assetId;
        this.attachment = attachment;
        this.chainId = chainId;
    }

    public Bytes recipient() {
        return recipient;
    }

    public long amount() {
        return amount;
    }

    public Bytes assetId() {
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
    public Bytes bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
