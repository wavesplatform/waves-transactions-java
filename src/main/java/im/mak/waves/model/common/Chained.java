package im.mak.waves.model.common;

@Deprecated //TODO remove the interface. All the new transaction versions will have a chainId
public interface Chained {

    byte chainId();

}
