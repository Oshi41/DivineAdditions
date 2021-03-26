package divineadditions.api;

import java.util.Map;

public interface IRifleCoreConfig {
    /**
     * Returns max item damage
     */
    int getDurability();

    /**
     * Cooldown between shots
     *
     * @return
     */
    int getCoolddown();

    /**
     * Get possible bullets
     *
     * @return
     */
    Map<String, Integer> getBullets();

    /**
     * Get possible catalysts
     *
     * @return
     */
    Map<String, Integer> getCatalysts();
}
