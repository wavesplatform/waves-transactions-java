package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Transaction;
import com.wavesplatform.scheme.components.Transfer;

public class MassTransferTransaction extends Transaction {

    public static final int TYPE = 11;

    private final Transfer[] transfers;
    private final Bytes assetId; //TODO type?
    private final String attachment; //TODO Bytes?

    public MassTransferTransaction(Transfer[] transfers, Bytes assetId, String attachment, long fee, long timestamp) {
        this(transfers, assetId, attachment, fee, timestamp, new Bytes[0], Bytes.empty());
    }

    public MassTransferTransaction(Transfer[] transfers, Bytes assetId, String attachment, long fee, long timestamp, Bytes[] proofs) {
        this(transfers, assetId, attachment, fee, timestamp, proofs, Bytes.empty());
    }

    public MassTransferTransaction(Transfer[] transfers, Bytes assetId, String attachment, long fee, long timestamp, Bytes id) {
        this(transfers, assetId, attachment, fee, timestamp, new Bytes[0], id);
    }

    public MassTransferTransaction(Transfer[] transfers, Bytes assetId, String attachment, long fee, long timestamp, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.transfers = transfers;
        this.assetId = assetId;
        this.attachment = attachment;
    }

    public Transfer[] transfers() {
        return transfers;
    }

    public Bytes assetId() {
        return assetId;
    }

    public String attachment() {
        return attachment;
    }

    @Override
    public Bytes bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString
    //TODO totalAmount? transactionCount?

}
