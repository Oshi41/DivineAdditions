package divineadditions.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Use this class for loot bonus of bullet entities
 */
public class EntityDamageSourceIndirectEnch extends EntityDamageSourceIndirect {
    private Map<Enchantment, Integer> enchantments;

    /**
     * Saves all possible enchantments here
     *
     * @param damageTypeIn     - damage type. See {@link DamageSource} for more info
     * @param source           - attacking entity
     * @param indirectEntityIn - thrower or owner of the attacking entity
     * @param enchantments     - enchantments for source entity
     */
    public EntityDamageSourceIndirectEnch(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn, Map<Enchantment, Integer> enchantments) {
        super(damageTypeIn, source, indirectEntityIn);
        this.enchantments = enchantments;
    }

    public static EntityDamageSourceIndirectEnch createThrowable(EntityThrowable bullet, Map<Enchantment, Integer> enchantments) {
        return new EntityDamageSourceIndirectEnch("thrown", bullet, bullet.getThrower(), enchantments);
    }

    /**
     * Returns current enchantment level
     *
     * @param enchantment
     * @return
     */
    public int getLevel(Enchantment enchantment) {
        return enchantments.getOrDefault(enchantment, 0);
    }

    /**
     * Gets all enchantments for creature
     *
     * @return
     */
    public Set<Enchantment> getAll() {
        return enchantments.keySet();
    }
}
