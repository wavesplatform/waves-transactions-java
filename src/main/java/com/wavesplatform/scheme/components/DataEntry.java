package com.wavesplatform.scheme.components;

import com.wavesplatform.crypto.Bytes;

public class DataEntry {

    private String key;
    private EntryType type;
    private Object value; //TODO чо делать-то?

    public DataEntry(String key, Bytes binary) {
        this.key = key;
        this.type = EntryType.BINARY;
        value = binary;
    }

    public DataEntry(String key, boolean bool) {
        this.key = key;
        this.type = EntryType.BOOLEAN;
        value = bool;
    }

    public DataEntry(String key, long integer) {
        this.key = key;
        this.type = EntryType.INTEGER;
        value = integer;
    }

    public DataEntry(String key, String string) {
        this.key = key;
        this.type = EntryType.STRING;
        value = string;
    }

    public String key() {
        return key;
    }

    public EntryType type() {
        return type;
    }

    public Object value() {
        return value;
    }

}
