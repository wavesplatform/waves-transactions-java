package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.crypto.base.Base64;
import im.mak.waves.model.common.Chained;
import im.mak.waves.model.common.Transaction;

public class IssueTransaction extends Transaction implements Chained {

    public static final int TYPE = 3;

    private final String name;
    private final String description;
    private final long quantity;
    private final int decimals;
    private final boolean reissuable;
    private final Base64 script;
    private final byte chainId;

    public IssueTransaction(String name, String description, long quantity, int decimals, boolean reissuable, Base64 script, long fee, long timestamp, byte chainId) {
        this(name, description, quantity, decimals, reissuable, script, fee, timestamp, chainId, new Base58[0], new Base58(Bytes.empty()));
    }

    public IssueTransaction(String name, String description, long quantity, int decimals, boolean reissuable, Base64 script, long fee, long timestamp, byte chainId, Base58[] proofs) {
        this(name, description, quantity, decimals, reissuable, script, fee, timestamp, chainId, proofs, new Base58(Bytes.empty()));
    }

    public IssueTransaction(String name, String description, long quantity, int decimals, boolean reissuable, Base64 script, long fee, long timestamp, byte chainId, Base58 id) {
        this(name, description, quantity, decimals, reissuable, script, fee, timestamp, chainId, new Base58[0], id);
    }

    public IssueTransaction(String name, String description, long quantity, int decimals, boolean reissuable, Base64 script, long fee, long timestamp, byte chainId, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
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
