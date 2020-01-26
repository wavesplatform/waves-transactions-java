package im.mak.waves.model.common;

public enum Type { //TODO use in transactions

    GENESIS(1),
    PAYMENT(2),
    ISSUE(3),
    TRANSFER(4),
    REISSUE(5),
    BURN(6),
    EXCHANGE(7),
    LEASE(8),
    LEASE_CANCEL(9),
    CREATE_ALIAS(10),
    MASS_TRANSFER(11),
    DATA(12),
    SET_SCRIPT(13),
    SPONSOR_FEE(14),
    SET_ASSET_SCRIPT(15),
    INVOKE_SCRIPT(16),
    UPDATE_ASSET_INFO(17);

    private final int value;

    Type(final int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}
