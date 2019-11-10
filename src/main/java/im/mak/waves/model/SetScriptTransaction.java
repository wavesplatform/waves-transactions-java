package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.crypto.base.Base64;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class SetScriptTransaction extends Transaction implements Chained {

    public static final int TYPE = 13;

    private final Base64 script;
    private final byte chainId;

    public SetScriptTransaction(Base64 script, long fee, long timestamp, byte chainId) {
        this(script, fee, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public SetScriptTransaction(Base64 script, long fee, long timestamp, byte chainId, Base58[] proofs) {
        this(script, fee, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public SetScriptTransaction(Base64 script, long fee, long timestamp, byte chainId, Base58 id) {
        this(script, fee, timestamp, chainId, new Base58[0], id);
    }

    public SetScriptTransaction(Base64 script, long fee, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.script = script;
        this.chainId = chainId;
    }

    public Base64 script() {
        return script;
    }

    @Override
    public byte chainId() {
        return chainId;
    }

    @Override
    public byte[] bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
