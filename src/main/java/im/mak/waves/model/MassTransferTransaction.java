package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Transaction;
import im.mak.waves.model.components.Transfer;

public class MassTransferTransaction extends Transaction {

    public static final int TYPE = 11;

    private final Transfer[] transfers;
    private final Base58 assetId; //TODO type?
    private final String attachment; //TODO Bytes?

    public MassTransferTransaction(Transfer[] transfers, Base58 assetId, String attachment, long fee, long timestamp) {
        this(transfers, assetId, attachment, fee, timestamp, new Base58[0], new Base58(Bytes.empty()));
    }

    public MassTransferTransaction(Transfer[] transfers, Base58 assetId, String attachment, long fee, long timestamp, Base58[] proofs) {
        this(transfers, assetId, attachment, fee, timestamp, proofs, new Base58(Bytes.empty()));
    }

    public MassTransferTransaction(Transfer[] transfers, Base58 assetId, String attachment, long fee, long timestamp, Base58 id) {
        this(transfers, assetId, attachment, fee, timestamp, new Base58[0], id);
    }

    public MassTransferTransaction(Transfer[] transfers, Base58 assetId, String attachment, long fee, long timestamp, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.transfers = transfers;
        this.assetId = assetId;
        this.attachment = attachment;
    }

    public Transfer[] transfers() {
        return transfers;
    }

    public Base58 assetId() {
        return assetId;
    }

    public String attachment() {
        return attachment;
    }

    @Override
    public byte[] bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString
    //TODO totalAmount? transactionCount?

}
