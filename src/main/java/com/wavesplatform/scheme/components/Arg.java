package com.wavesplatform.scheme.components;

import com.wavesplatform.crypto.Bytes;

public class Arg {

    private Object value; //TODO typed

    public Arg(Bytes value) {
        this.value = value;
    }

    public Arg(boolean value) {
        this.value = value;
    }

    public Arg(long value) {
        this.value = value;
    }

    public Arg(String value) {
        this.value = value;
    }

    public Object value() {
        return value;
    }
}
