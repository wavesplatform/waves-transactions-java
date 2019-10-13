package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class SetScriptTransaction extends Transaction implements Chained {

    public static final int TYPE = 13;

    private final String script;
    private final byte chainId;

    public SetScriptTransaction(String script, long fee, long timestamp, byte chainId) {
        this(script, fee, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public SetScriptTransaction(String script, long fee, long timestamp, byte chainId, Bytes[] proofs) {
        this(script, fee, timestamp, chainId, proofs, Bytes.empty());
    }

    public SetScriptTransaction(String script, long fee, long timestamp, byte chainId, Bytes id) {
        this(script, fee, timestamp, chainId, new Bytes[0], id);
    }

    public SetScriptTransaction(String script, long fee, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.script = script;
        this.chainId = chainId;
    }

    public String script() {
        return script;
    }

    @Override
    public byte chainId() {
        return chainId;
    }

    @Override
    public Bytes bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
