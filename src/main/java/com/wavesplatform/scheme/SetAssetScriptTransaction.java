package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class SetAssetScriptTransaction extends Transaction implements Chained {

    public static final int TYPE = 15;

    private final Bytes assetId;
    private final String script;
    private final byte chainId;

    public SetAssetScriptTransaction(Bytes assetId, String script, long fee, long timestamp, byte chainId) {
        this(assetId, script, fee, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public SetAssetScriptTransaction(Bytes assetId, String script, long fee, long timestamp, byte chainId, Bytes[] proofs) {
        this(assetId, script, fee, timestamp, chainId, proofs, Bytes.empty());
    }

    public SetAssetScriptTransaction(Bytes assetId, String script, long fee, long timestamp, byte chainId, Bytes id) {
        this(assetId, script, fee, timestamp, chainId, new Bytes[0], id);
    }

    public SetAssetScriptTransaction(Bytes assetId, String script, long fee, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.assetId = assetId;
        this.script = script;
        this.chainId = chainId;
    }

    public Bytes assetId() {
        return assetId;
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
