package divineadditions.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EntityDamageSourceIndirect;

import javax.annotation.Nullable;

/**
 * Use this class for loot bonus of bullet entities
 */
public class EntityDamageSourceIndirectLooting extends EntityDamageSourceIndirect {
    private final int looting;

    public EntityDamageSourceIndirectLooting(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn, int looting) {
        super(damageTypeIn, source, indirectEntityIn);
        this.looting = looting;
    }

    public static EntityDamageSourceIndirectLooting createThrowable(EntityThrowable bullet, int looting) {
        return new EntityDamageSourceIndirectLooting("thrown", bullet, bullet.getThrower(), looting);
    }

    public int getLooting() {
        return looting;
    }
}
