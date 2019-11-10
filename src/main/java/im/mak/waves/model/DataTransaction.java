package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.model.common.Transaction;
import im.mak.waves.model.components.DataEntry;

import java.util.List;

public class DataTransaction extends Transaction {

    public static final int TYPE = 12;

    private final List<DataEntry> data; //TODO array or List? And in other txs too...

    public DataTransaction(List<DataEntry> data, long fee, long timestamp) {
        this(data, fee, timestamp, new Base58[0], new Base58(Bytes.empty()));
    }

    public DataTransaction(List<DataEntry> data, long fee, long timestamp, Base58[] proofs) {
        this(data, fee, timestamp, proofs, new Base58(Bytes.empty()));
    }

    public DataTransaction(List<DataEntry> data, long fee, long timestamp, Base58 id) {
        this(data, fee, timestamp, new Base58[0], id);
    }

    public DataTransaction(List<DataEntry> data, long fee, long timestamp, Base58[] proofs, Base58 id) {
        super(TYPE, fee, new Base58(Bytes.empty()), timestamp, proofs, id);
        this.data = data;
    }

    public List<DataEntry> data() {
        return data;
    }

    @Override
    public byte[] bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
