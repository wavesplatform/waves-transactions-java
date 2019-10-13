package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class CreateAliasTransaction extends Transaction implements Chained {

    public static final int TYPE = 10;

    private final String alias;
    private final byte chainId;

    public CreateAliasTransaction(String alias, long fee, long timestamp, byte chainId) {
        this(alias, fee, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public CreateAliasTransaction(String alias, long fee, long timestamp, byte chainId, Bytes[] proofs) {
        this(alias, fee, timestamp, chainId, proofs, Bytes.empty());
    }

    public CreateAliasTransaction(String alias, long fee, long timestamp, byte chainId, Bytes id) {
        this(alias, fee, timestamp, chainId, new Bytes[0], id);
    }

    public CreateAliasTransaction(String alias, long fee, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
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
    public Bytes bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
