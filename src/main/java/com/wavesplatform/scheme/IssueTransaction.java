package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Chained;
import com.wavesplatform.scheme.common.Transaction;

public class IssueTransaction extends Transaction implements Chained {

    public static final int TYPE = 3;

    private final String name;
    private final String description;
    private final long quantity;
    private final int decimals;
    private final boolean reissuable;
    private final String script;
    private final byte chainId;

    public IssueTransaction(String name, String description, long quantity, int decimals, boolean reissuable, String script, long fee, long timestamp, byte chainId) {
        this(name, description, quantity, decimals, reissuable, script, fee, timestamp, chainId, new Bytes[0], Bytes.empty());
    }

    public IssueTransaction(String name, String description, long quantity, int decimals, boolean reissuable, String script, long fee, long timestamp, byte chainId, Bytes[] proofs) {
        this(name, description, quantity, decimals, reissuable, script, fee, timestamp, chainId, proofs, Bytes.empty());
    }

    public IssueTransaction(String name, String description, long quantity, int decimals, boolean reissuable, String script, long fee, long timestamp, byte chainId, Bytes id) {
        this(name, description, quantity, decimals, reissuable, script, fee, timestamp, chainId, new Bytes[0], id);
    }

    public IssueTransaction(String name, String description, long quantity, int decimals, boolean reissuable, String script, long fee, long timestamp, byte chainId, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.script = script;
        this.chainId = chainId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public long quantity() {
        return quantity;
    }

    public int decimals() {
        return decimals;
    }

    public boolean isReissuable() {
        return reissuable;
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
