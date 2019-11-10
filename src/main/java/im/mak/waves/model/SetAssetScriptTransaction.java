package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.crypto.base.Base64;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class SetAssetScriptTransaction extends Transaction implements Chained {

    public static final int TYPE = 15;

    private final Base58 assetId;
    private final Base64 script;
    private final byte chainId;

    public SetAssetScriptTransaction(Base58 assetId, Base64 script, long fee, long timestamp, byte chainId) {
        this(assetId, script, fee, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public SetAssetScriptTransaction(Base58 assetId, Base64 script, long fee, long timestamp, byte chainId, Base58[] proofs) {
        this(assetId, script, fee, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public SetAssetScriptTransaction(Base58 assetId, Base64 script, long fee, long timestamp, byte chainId, Base58 id) {
        this(assetId, script, fee, timestamp, chainId, new Base58[0], id);
    }

    public SetAssetScriptTransaction(Base58 assetId, Base64 script, long fee, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.assetId = assetId;
        this.script = script;
        this.chainId = chainId;
    }

    public Base58 assetId() {
        return assetId;
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
