package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class CreateAliasTransaction extends Transaction implements Chained {

    public static final int TYPE = 10;

    private final String alias;
    private final byte chainId;

    public CreateAliasTransaction(String alias, long fee, long timestamp, byte chainId) {
        this(alias, fee, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public CreateAliasTransaction(String alias, long fee, long timestamp, byte chainId, Base58[] proofs) {
        this(alias, fee, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public CreateAliasTransaction(String alias, long fee, long timestamp, byte chainId, Base58 id) {
        this(alias, fee, timestamp, chainId, new Base58[0], id);
    }

    public CreateAliasTransaction(String alias, long fee, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.alias = alias;
        this.chainId = chainId;
    }

    public String alias() {
        return alias;
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
