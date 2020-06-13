package im.mak.waves.transactions;

import im.mak.waves.crypto.account.PublicKey;
import im.mak.waves.transactions.common.Amount;
import im.mak.waves.transactions.common.Asset;
import im.mak.waves.transactions.common.Proof;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BurnTransaction extends Transaction {

    public static final int TYPE = 6;
    public static final int LATEST_VERSION = 3;
    public static final long MIN_FEE = 100_000;

    private final Amount amount;

    public BurnTransaction(PublicKey sender, Amount amount, byte chainId, long fee, long timestamp, int version) {
        this(sender, amount, chainId, fee, timestamp, version, Proof.emptyList());
    }

    public BurnTransaction(PublicKey sender, Amount amount, byte chainId, long fee, long timestamp, int version, List<Proof> proofs) {
        super(TYPE, version, chainId, sender, fee, Asset.WAVES, timestamp, proofs);
        if (amount.asset().isWaves())
            throw new IllegalArgumentException("Can't be Waves");

        this.amount = amount;
    }

    public static BurnTransaction fromBytes(byte[] bytes) throws IOException {
        return (BurnTransaction) Transaction.fromBytes(bytes);
    }

    public static BurnTransaction fromJson(String json) throws IOException {
        return (BurnTransaction) Transaction.fromJson(json);
    }

    public static BurnTransactionBuilder with(Amount amount) {
        return new BurnTransactionBuilder(amount);
    }

    public Amount amount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BurnTransaction that = (BurnTransaction) o;
        return this.amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), amount);
    }

    public static class BurnTransactionBuilder
            extends TransactionBuilder<BurnTransactionBuilder, BurnTransaction> {
        private final Amount amount;

        protected BurnTransactionBuilder(Amount amount) {
            super(LATEST_VERSION, MIN_FEE);
            this.amount = amount;
        }

        protected BurnTransaction _build() {
            return new BurnTransaction(sender, amount, chainId, fee, timestamp, version);
        }
    }

}
