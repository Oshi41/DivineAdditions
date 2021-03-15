package divineadditions.api;

public interface IProxy {
    /**
     * is this a dedicated server?
     *
     * @return true if this is a dedicated server, false otherwise
     */
    boolean isDedicatedServer();

    void pre();

    void init();

    void post();
}


