package divineadditions.entity;

import divineadditions.DivineAdditions;
import divineadditions.api.ISkinProvider;
import divineadditions.capability.knowledge.IKnowledgeInfo;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.holders.Blocks;
import divineadditions.holders.Items;
import divineadditions.utils.EntityAttributeHelper;
import divineadditions.utils.InventoryHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityAncientVillager extends EntityCreature implements ISkinProvider {
    private static final DataParameter<String> SKIN = EntityDataManager.createKey(EntityAncientVillager.class, DataSerializers.STRING);
    private static List<ResourceLocation> skins = new ArrayList<ResourceLocation>() {{
        add(new ResourceLocation(DivineAdditions.MOD_ID, "textures/entity/ancient_villager/ancient_villager_old.png"));
        add(new ResourceLocation(DivineAdditions.MOD_ID, "textures/entity/ancient_villager/ancient_villager_old.png"));
        add(new ResourceLocation(DivineAdditions.MOD_ID, "textures/entity/ancient_villager/ancient_villager_war.png"));
    }};

    private EntityLivingBase summoner;
    private EntityAIFollow aiFollow;

    public EntityAncientVillager(World worldIn) {
        super(worldIn);
    }

    public EntityAncientVillager(World worldIn, EntityLivingBase summoner) {
        this(worldIn);
        this.summoner = summoner;

        worldIn.rand.setSeed(worldIn.rand.nextLong());
        setSkin(skins.get(worldIn.rand.nextInt(skins.size())));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SKIN, "");
    }

    @Override
    protected void initEntityAI() {
        aiFollow = new EntityAIFollow(this, 1.0D, 3.0F, 7.0F);
        this.tasks.addTask(4, aiFollow);
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIWanderAvoidWater(this, 0.6D));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        EntityAttributeHelper.applyAttributes(DivineAdditionsConfig.mobsConfig.ancient_villager_attrs, this);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (summoner == null)
            return;

        switch (ticksExisted) {
            case 1:

                if (!world.isRemote)
                    summoner.sendMessage(new TextComponentTranslation("divineadditions.message.ancient_villager.save" + world.rand.nextInt(3)));
                break;

            case 20 * 30:
                if (!world.isRemote) {
                    IKnowledgeInfo iKnowledgeInfo = summoner.getCapability(IKnowledgeInfo.KnowledgeCapability, null);
                    if (iKnowledgeInfo != null && iKnowledgeInfo.getLevel() < 1) {
                        summoner.sendMessage(new TextComponentTranslation("divineadditions.message.ancient_villager.gift" + world.rand.nextInt(3)));
                        List<ItemStack> stacks = Arrays.asList(new ItemStack(Items.book_of_knowledge_1), new ItemStack(Blocks.forge));
                        InventoryHelper.insert(summoner, stacks.toArray(new ItemStack[0]));
                    }
                }
                break;


            case 20 * 60:
                if (!world.isRemote) {
                    summoner.sendMessage(new TextComponentTranslation("divineadditions.message.ancient_villager.need_to_go"));
                }
                this.tasks.removeTask(aiFollow);
                this.tasks.addTask(1, new EntityAIPanic(this, 1.2));
                break;


            case 20 * 80:

                if (!world.isRemote) {
                    attemptTeleport(posX, 0, posY);
                    setDead();
                } else {
                    this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

                    for (int i = 0; i < 127; i++) {
                        world.spawnParticle(
                                EnumParticleTypes.PORTAL,
                                posX + (rand.nextFloat() - rand.nextFloat()) * 2,
                                posY + (rand.nextFloat() - rand.nextFloat()) * 2,
                                posZ + (rand.nextFloat() - rand.nextFloat()) * 2,
                                rand.nextFloat() - rand.nextFloat(),
                                rand.nextFloat() - rand.nextFloat(),
                                rand.nextFloat() - rand.nextFloat()
                        );
                    }
                }

                break;
        }
    }

    @Nullable
    @Override
    public EntityLivingBase getRevengeTarget() {
        return summoner;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        setSkin(new ResourceLocation(compound.getString("Skin")));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);
        nbt.setString("Skin", getSkin().toString());
        return nbt;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
    }

    @Override
    public ResourceLocation getSkin() {
        return new ResourceLocation(dataManager.get(SKIN));
    }

    @Override
    public void setSkin(ResourceLocation id) {
        dataManager.set(SKIN, id.toString());
    }
}
