package im.mak.waves.model;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.base.Base58;

@Deprecated //TODO стоит ли? Ведь уже не используется
public class PaymentTransaction {

    public PaymentTransaction(int type, long fee, long timestamp, Base58 signature) {
        this(new Base58(Bytes.empty()), type, fee, timestamp, signature);
    }

    public PaymentTransaction(Base58 id, int type, long fee, long timestamp, Base58 signature) {
        //TODO
    }

    //TODO hashCode, equals, toString
    //TODO fields; sender, senderPK; id as signature

    public int type() {
        return 0;
    }

    public Base58 id() {
        return null;
    }

    public long fee() {
        return 0;
    }

    public long timestamp() {
        return 0;
    }

    public Base58[] proofs() {
        return new Base58[0];
    }

    public byte[] bodyBytes() {
        return null;
    }


}
