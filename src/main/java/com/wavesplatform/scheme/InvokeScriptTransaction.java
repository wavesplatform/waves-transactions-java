package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Transaction;
import com.wavesplatform.scheme.components.Function;
import com.wavesplatform.scheme.components.Payment;

import java.util.List;

public class InvokeScriptTransaction extends Transaction {

    public static final int TYPE = 16;

    private final String dApp;
    private final Function call;
    private final List<Payment> payment;

    public InvokeScriptTransaction(String dApp, Function call, List<Payment> payment, long fee, Bytes feeAssetId, long timestamp) {
        this(dApp, call, payment, fee, feeAssetId, timestamp, new Bytes[0], Bytes.empty());
    }

    public InvokeScriptTransaction(String dApp, Function call, List<Payment> payment, long fee, Bytes feeAssetId, long timestamp, Bytes[] proofs) {
        this(dApp, call, payment, fee, feeAssetId, timestamp, proofs, Bytes.empty());
    }

    public InvokeScriptTransaction(String dApp, Function call, List<Payment> payment, long fee, Bytes feeAssetId, long timestamp, Bytes id) {
        this(dApp, call, payment, fee, feeAssetId, timestamp, new Bytes[0], id);
    }

    public InvokeScriptTransaction(String dApp, Function call, List<Payment> payment, long fee, Bytes feeAssetId, long timestamp, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, feeAssetId, timestamp, proofs, id);
        this.dApp = dApp;
        this.call = call;
        this.payment = payment;
    }

    public String name() {
        return dApp;
    }

    public Function description() {
        return call;
    }

    public List<Payment> quantity() {
        return payment;
    }

    @Override
    public Bytes bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
