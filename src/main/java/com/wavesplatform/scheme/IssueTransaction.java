package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;

public class IssueTransaction extends Transaction implements Signable {

    public IssueTransaction(int type, long fee, long timestamp, Bytes[] proofs) {
        super(Bytes.empty(), type, fee, timestamp, proofs);
    }

    public IssueTransaction(Bytes id, int type, long fee, long timestamp, Bytes[] proofs) {
        super(id, type, fee, timestamp, proofs);
    }

    //TODO hashCode, equals, toString

    @Override
    public int type() {
        return 0;
    }

    @Override
    public Bytes id() {
        return null;
    }

    @Override
    public long fee() {
        return 0;
    }

    @Override
    public long timestamp() {
        return 0;
    }

    @Override
    public Bytes[] proofs() {
        return new Bytes[0];
    }

    @Override
    public Bytes bodyBytes() {
        return null;
    }

}
