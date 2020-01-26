package im.mak.waves.model.common;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.Hash;
import im.mak.waves.crypto.account.Seed;
import im.mak.waves.crypto.account.PrivateKey;
import im.mak.waves.crypto.base.Base58;

public abstract class Transaction {

    //TODO sender, senderPk, version? chainId?
    private int version; //TODO int or less? How to set latest?
    private Base58 id;
    private int type; //TODO int or less?
    private long fee;
    private Base58 feeAssetId;
    private long timestamp;
    private Base58[] proofs;

    public static abstract class TransactionBuilder {
        protected int version;
        protected Base58 id;
        protected int type;
        protected long fee;
        protected Base58 feeAssetId;
        protected long timestamp;
        protected Base58[] proofs;

        public TransactionBuilder version(int version) {
            this.version = version;
            return this;
        }

        public TransactionBuilder id(Base58 id) {
            this.id = id;
            return this;
        }

        public TransactionBuilder type(int type) {
            this.type = type;
            return this;
        }

        public TransactionBuilder fee(long fee) {
            this.fee = fee;
            return this;
        }

        public TransactionBuilder feeAssetId(Base58 feeAssetId) {
            this.feeAssetId = feeAssetId;
            return this;
        }

        public TransactionBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TransactionBuilder proofs(Base58[] proofs) {
            this.proofs = proofs;
            return this;
        }

        //TODO public abstract create()
    }

    public Transaction(int type, long fee, Base58 feeAssetId, long timestamp) {
        this(type, fee, feeAssetId, timestamp, new Base58[0], new Base58(Bytes.empty()));
    }

    public Transaction(int type, long fee, Base58 feeAssetId, long timestamp, Base58[] proofs) {
        this(type, fee, feeAssetId, timestamp, proofs, new Base58(Bytes.empty()));
    }

    public Transaction(int type, long fee, Base58 feeAssetId, long timestamp, Base58 id) {
        this(type, fee, feeAssetId, timestamp, new Base58[0], id);
    }

    public Transaction(int type, long fee, Base58 feeAssetId, long timestamp, Base58[] proofs, Base58 id) {
        //TODO validate args
        this.id = id; //TODO copy?
        this.type = type;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.timestamp = timestamp;
        this.proofs = proofs; //TODO copy?
    }

    public Base58 id() {
        return new Base58(Hash.blake(bodyBytes()));
    }

    public int type() {
        return type;
    }

    public long fee() {
        return fee;
    }

    public Base58 feeAssetId() {
        return feeAssetId;
    }

    public long timestamp() {
        return timestamp;
    }

    public Base58[] proofs() {
        return proofs;
    }

    public Base58 sign(Seed seed) {
        return sign(seed.privateKey());
    }

    public Base58 sign(PrivateKey privateKey) {
        //TODO add/update proofs
        return new Base58(privateKey.sign(bodyBytes()));
    }

    public abstract byte[] bodyBytes(); //TODO implement in the all transactions

    private byte[] baseBytes() {
        return new byte[0];
    }

}
