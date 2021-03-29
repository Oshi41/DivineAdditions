package divineadditions.api;

public interface IItemCapacity {

    /**
     * Max stack size for current pedestal
     *
     * @return
     */
    default int getStackSize() {
        return 64;
    }

    /**
     * Size of IItemHandler
     *
     * @return
     */
    default int getSlotCount() {
        return 1;
    }
}
