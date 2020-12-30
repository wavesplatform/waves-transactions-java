package com.wavesplatform.transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.common.Proof;

import java.io.IOException;
import java.util.Objects;

@Deprecated
public class PaymentTransaction extends Transaction {

    public static final int TYPE = 2;
    public static final int LATEST_VERSION = 1;
    public static final long MIN_FEE = 1;

    private final Address recipient;
    private final long amount;

    public PaymentTransaction(PublicKey sender, Address recipient, long amount) {
        this(sender, recipient, amount, Amount.of(MIN_FEE), System.currentTimeMillis(), null);
    }

    public PaymentTransaction(
            PublicKey sender, Address recipient, long amount, Amount fee, long timestamp, Proof signature) {
        super(TYPE, LATEST_VERSION, recipient.chainId(), sender, fee, timestamp,
                signature == null ? Proof.emptyList() : Proof.list(signature));

        this.recipient = recipient;
        this.amount =  amount;
    }

    public static PaymentTransaction fromBytes(byte[] bytes) throws IOException {
        return (PaymentTransaction) Transaction.fromBytes(bytes);
    }

    public static PaymentTransaction fromJson(String json) throws IOException {
        return (PaymentTransaction) Transaction.fromJson(json);
    }

    @Override
    public Id id() {
        return Id.as(proofs().get(0).bytes());
    }

    public Address recipient() {
        return recipient;
    }

    public long amount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PaymentTransaction that = (PaymentTransaction) o;
        return this.recipient.equals(that.recipient) && this.amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), recipient, amount);
    }

}
