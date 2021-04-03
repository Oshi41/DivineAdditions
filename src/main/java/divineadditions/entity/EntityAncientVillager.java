package divineadditions.entity;

import divineadditions.DivineAdditions;
import divineadditions.api.ISkinProvider;
import divineadditions.capability.knowledge.IKnowledgeInfo;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.holders.Blocks;
import divineadditions.holders.Items;
import divineadditions.utils.EntityAttributeHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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
    public void onLivingUpdate() {
        super.onLivingUpdate();

        final int step = 10;

        switch (ticksExisted) {
            case 1:
                sendMsg(new TextComponentTranslation("divineadditions.message.ancient_villager.save" + world.getTotalWorldTime() % 2));
                break;

            case 20 * step:
                if (!world.isRemote && summoner != null) {
                    IKnowledgeInfo iKnowledgeInfo = summoner.getCapability(IKnowledgeInfo.KnowledgeCapability, null);
                    if (iKnowledgeInfo != null && iKnowledgeInfo.getLevel() < 1) {
                        sendMsg(new TextComponentTranslation("divineadditions.message.ancient_villager.gift" + world.getTotalWorldTime() % 2));
                        sendMsg(insertStacks(summoner, Arrays.asList(new ItemStack(Items.book_of_knowledge_1), new ItemStack(Blocks.forge))));
                    }
                }
                break;


            case 20 * step * 2:
                sendMsg(new TextComponentTranslation("divineadditions.message.ancient_villager.need_to_go"));
                this.tasks.removeTask(aiFollow);
                this.tasks.addTask(1, new EntityAIPanic(this, 1.2));
                break;


            case 20 * step * 3:
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

    private void sendMsg(ITextComponent text) {
        if (world.isRemote || summoner == null || text == null)
            return;

        summoner.sendMessage(text);
    }

    private ITextComponent insertStacks(EntityLivingBase entity, List<ItemStack> stacks) {
        stacks = new ArrayList<>(stacks);

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            InventoryEnderChest chest = player.getInventoryEnderChest();

            while (!stacks.isEmpty()) {
                ItemStack first = stacks.get(0);

                if (chest.addItem(first) != ItemStack.EMPTY && !player.inventory.addItemStackToInventory(first)) {
                    InventoryHelper.spawnItemStack(world, entity.posX, entity.posY, entity.posZ, first);
                }

                stacks.remove(0);
            }

            return new TextComponentTranslation("divineadditions.message.ancient_villager.gift.end_chest");
        }

        return null;
    }
}
