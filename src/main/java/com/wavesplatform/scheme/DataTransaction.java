package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.scheme.common.Transaction;
import com.wavesplatform.scheme.components.DataEntry;

import java.util.List;

public class DataTransaction extends Transaction {

    public static final int TYPE = 12;

    private final List<DataEntry> data; //TODO array or List? And in other fields too...

    public DataTransaction(List<DataEntry> data, long fee, long timestamp) {
        this(data, fee, timestamp, new Bytes[0], Bytes.empty());
    }

    public DataTransaction(List<DataEntry> data, long fee, long timestamp, Bytes[] proofs) {
        this(data, fee, timestamp, proofs, Bytes.empty());
    }

    public DataTransaction(List<DataEntry> data, long fee, long timestamp, Bytes id) {
        this(data, fee, timestamp, new Bytes[0], id);
    }

    public DataTransaction(List<DataEntry> data, long fee, long timestamp, Bytes[] proofs, Bytes id) {
        super(TYPE, fee, Bytes.empty(), timestamp, proofs, id);
        this.data = data;
    }

    public List<DataEntry> data() {
        return data;
    }

    @Override
    public Bytes bodyBytes() {
        return null;
    }

    //TODO hashCode, equals, toString

}
