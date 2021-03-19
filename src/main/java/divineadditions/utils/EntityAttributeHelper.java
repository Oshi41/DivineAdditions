package divineadditions.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import openmods.reflection.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityAttributeHelper {
    private static final Map<String, IAttribute> wellKnownAttributes;

    static {
        wellKnownAttributes = new HashMap<>();

        register(SharedMonsterAttributes.ARMOR);
        register(SharedMonsterAttributes.ARMOR_TOUGHNESS);

        register(SharedMonsterAttributes.ATTACK_DAMAGE);
        register(SharedMonsterAttributes.ATTACK_SPEED);
        register(SharedMonsterAttributes.MOVEMENT_SPEED);
        register(SharedMonsterAttributes.FLYING_SPEED);

        register(SharedMonsterAttributes.MAX_HEALTH);
        register(SharedMonsterAttributes.FOLLOW_RANGE);
        register(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);

        register(EntityPlayer.REACH_DISTANCE);
        register(EntityLivingBase.SWIM_SPEED);

        try {
            Field field = ReflectionHelper.getField(AbstractHorse.class, "JUMP_STRENGTH");
            field.setAccessible(true);
            IAttribute attr = (IAttribute) field.get(null);
            register(attr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Feel free to register own attribute
     *
     * @param attribute
     */
    public static void register(IAttribute attribute) {
        wellKnownAttributes.put(attribute.getName(), attribute);
    }

    /**
     * Aplying current attributes toentity
     *
     * @param attrMap
     * @param entity
     */
    public static void applyAttributes(Map<String, Float> attrMap, EntityLivingBase entity) {
        if (attrMap == null || attrMap.isEmpty() || entity == null)
            return;

        attrMap.forEach((name, value) -> {
            IAttribute attribute = wellKnownAttributes.get(name);
            if (attribute == null)
                return;

            IAttributeInstance instance = entity.getEntityAttribute(attribute);
            if (instance == null) {
                entity.getAttributeMap().registerAttribute(attribute);
                instance = entity.getEntityAttribute(attribute);
            }

            instance.setBaseValue(value);
        });
    }
}
