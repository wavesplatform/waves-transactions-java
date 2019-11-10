package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Transaction;
import im.mak.waves.model.components.Function;
import im.mak.waves.model.components.Payment;

import java.util.List;

public class InvokeScriptTransaction extends Transaction {

    public static final int TYPE = 16;

    private final String dApp;
    private final Function call;
    private final List<Payment> payment;

    public InvokeScriptTransaction(String dApp, Function call, List<Payment> payment, long fee, Base58 feeAssetId, long timestamp) {
        this(dApp, call, payment, fee, feeAssetId, timestamp, new Base58[0], new Base58(Bytes.empty()));
    }

    public InvokeScriptTransaction(String dApp, Function call, List<Payment> payment, long fee, Base58 feeAssetId, long timestamp, Base58[] proofs) {
        this(dApp, call, payment, fee, feeAssetId, timestamp, proofs, new Base58(Bytes.empty()));
    }

    public InvokeScriptTransaction(String dApp, Function call, List<Payment> payment, long fee, Base58 feeAssetId, long timestamp, Base58 id) {
        this(dApp, call, payment, fee, feeAssetId, timestamp, new Base58[0], id);
    }

    public InvokeScriptTransaction(String dApp, Function call, List<Payment> payment, long fee, Base58 feeAssetId, long timestamp, Base58[] proofs, Base58 id) {
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
    public byte[] bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
