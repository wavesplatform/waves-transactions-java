package com.wavesplatform.scheme;

import com.wavesplatform.crypto.Bytes;

@Deprecated //TODO стоит ли? Ведь уже не используется
public class PaymentTransaction {

    public PaymentTransaction(int type, long fee, long timestamp, Bytes signature) {
        this(Bytes.empty(), type, fee, timestamp, signature);
    }

    public PaymentTransaction(Bytes id, int type, long fee, long timestamp, Bytes signature) {
        //TODO
    }

    //TODO hashCode, equals, toString
    //TODO fields; sender, senderPK; id as signature

    public int type() {
        return 0;
    }

    public Bytes id() {
        return null;
    }

    public long fee() {
        return 0;
    }

    public long timestamp() {
        return 0;
    }

    public Bytes[] proofs() {
        return new Bytes[0];
    }

    public Bytes bodyBytes() {
        return null;
    }


}
