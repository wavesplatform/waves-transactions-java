package com.wavesplatform.transactions;

import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.common.Proof;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class InvokeExpressionTransaction extends Transaction {
    public static final int TYPE = 18;
    public static final int LATEST_VERSION = 1;
    public static final long MIN_FEE = 500_000;

    private final Base64String expression;

    public InvokeExpressionTransaction(PublicKey sender, Base64String expression,
                                       byte chainId, Amount fee, long timestamp, int version, List<Proof> proofs) {
        super(TYPE, version, chainId, sender, fee, timestamp, proofs);

        this.expression = requireNonNull(expression, "expression can't be null");
    }

    public Base64String expression() {
        return expression;
    }

    public static InvokeExpressionTransaction fromBytes(byte[] bytes) throws IOException {
        return (InvokeExpressionTransaction) Transaction.fromBytes(bytes);
    }

    public static InvokeExpressionTransaction fromJson(String json) throws IOException {
        return (InvokeExpressionTransaction) Transaction.fromJson(json);
    }

    public static InvokeExpressionTransactionBuilder builder(Base64String expression) {
        return new InvokeExpressionTransactionBuilder(expression);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InvokeExpressionTransaction that = (InvokeExpressionTransaction) o;
        return this.expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression);
    }

    public static class InvokeExpressionTransactionBuilder
            extends TransactionBuilder<InvokeExpressionTransactionBuilder, InvokeExpressionTransaction> {
        private final Base64String expression;

        protected InvokeExpressionTransactionBuilder(Base64String expression) {
            super(LATEST_VERSION, MIN_FEE);
            this.expression = expression;
        }


        protected InvokeExpressionTransaction _build() {
            return new InvokeExpressionTransaction(sender, expression, chainId, feeWithExtra(),
                    timestamp, version, Proof.emptyList());
        }
    }
}
