package im.mak.waves.model;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;
import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.account.Address;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;
import im.mak.waves.model.common.Type;
import im.mak.waves.model.exceptions.BytesParseException;

import java.lang.invoke.MethodHandles;

public class TransferTransaction extends Transaction implements Chained {

    public static final Type TYPE = Type.TRANSFER;

    public static TransferTransaction fromBytes(byte[] bytes) throws InvalidProtocolBufferException, BytesParseException {
        TransactionOuterClass.Transaction tx = TransactionOuterClass.Transaction.parseFrom(bytes);
        return fromProtobuf(tx);
    }

    public static TransferTransaction fromProtobuf(TransactionOuterClass.Transaction instance) throws BytesParseException {
        if (!instance.hasTransfer())
            throw new BytesParseException("Parsed transaction is not a " + MethodHandles.lookup().lookupClass().getSimpleName());

//TODO        instance.getSenderPublicKey()
//TODO        instance.getVersion()
        return new TransferTransaction(
                Address.as(instance.getTransfer().getRecipient().toByteArray()),
                instance.getTransfer().getAmount().getAmount(),
                new Base58(instance.getTransfer().getAmount().getAssetId().toByteArray()),
                instance.getTransfer().getAttachment().getStringValue(),
                instance.getFee().getAmount(),
                new Base58(instance.getFee().getAssetId().toByteArray()),
                instance.getTimestamp(),
                (byte) instance.getChainId()
        );
    }

    public static TransferTransaction fromJson(String json) {
        return null; //TODO implement
    }

    public static TransferTransactionBuilder with() {
        return new TransferTransactionBuilder();
    }

    public static class TransferTransactionBuilder extends TransactionBuilder {
        private Address recipient;
        private long amount;
        private Base58 assetId;
        private String attachment;
        private byte chainId;

        public TransferTransactionBuilder recipient(Address recipient) {
            this.recipient = recipient;
            return this;
        }

        public TransferTransactionBuilder amount(long amount) {
            this.amount = amount;
            return this;
        }

        public TransferTransactionBuilder assetId(Base58 assetId) {
            this.assetId = assetId;
            return this;
        }

        public TransferTransactionBuilder attachment(String attachment) {
            this.attachment = attachment;
            return this;
        }

        public TransferTransactionBuilder chainId(byte chainId) {
            this.chainId = chainId;
            return this;
        }

        public TransferTransaction create() {
            //TODO get chainId from global context
            return new TransferTransaction(recipient, amount, assetId, attachment, fee, feeAssetId, timestamp, chainId, proofs, id);
        }
    }

    private final Address recipient; //TODO AddressOrAlias?
    private final long amount;
    private final Base58 assetId; //TODO type?
    private final String attachment; //TODO typed attachment or legacy bytes
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
        super(TYPE.value(), fee, feeAssetId, timestamp, proofs, id);
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
        return null; //TODO implement protobuf?
    }

    //TODO hashCode, equals, toString

    public TransactionOuterClass.Transaction toProtobuf() {
        return null; //TODO implement
    }

    public String toJson() {
        return null; //TODO implement
    }

}
