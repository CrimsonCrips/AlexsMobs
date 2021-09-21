package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class EntityBaldEagle extends TamableAnimal implements IFollower {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_CAP = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final Ingredient TEMPT_ITEMS = Ingredient.of(Items.ROTTEN_FLESH, AMItemRegistry.FISH_OIL);
    public float prevAttackProgress;
    public float attackProgress;
    public float prevFlyProgress;
    public float flyProgress;
    public float prevTackleProgress;
    public float tackleProgress;
    public float prevSwoopProgress;
    public float swoopProgress;
    public float prevFlapAmount;
    public float flapAmount;
    public float birdPitch = 0;
    public float prevBirdPitch = 0;
    public float prevSitProgress;
    public float sitProgress;
    private boolean isLandNavigator;
    private int timeFlying;
    private BlockPos orbitPos = null;
    private double orbitDist = 5D;
    private boolean orbitClockwise = false;
    private int passengerTimer = 0;
    private int launchTime = 0;
    private int lastPlayerControlTime = 0;
    private int returnControlTime = 0;
    private int tackleCapCooldown = 0;
    private boolean controlledFlag = false;
    private int chunkLoadCooldown;
    private int stillTicksCounter = 0;

    protected EntityBaldEagle(EntityType<? extends TamableAnimal> type, Level worldIn) {
        super(type, worldIn);
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public static boolean canEagleSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random random) {
        return worldIn.getRawBrightness(pos, 0) > 8;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this) {
            public boolean canUse() {
                return super.canUse() && (EntityBaldEagle.this.getAirSupply() < 30 || EntityBaldEagle.this.getTarget() == null || !EntityBaldEagle.this.getTarget().isInWaterOrBubble() && EntityBaldEagle.this.getY() > EntityBaldEagle.this.getTarget().getY());
            }
        });
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FlyingAIFollowOwner(this, 1.0D, 25.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new AITackle());
        this.goalSelector.addGoal(4, new AILandOnGlove());
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1D, TEMPT_ITEMS.merge(ImmutableList.of(Ingredient.of(ItemTags.getAllTags().getTag(AMTagRegistry.BALD_EAGLE_TAMEABLES)))), true));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, Ingredient.of(ItemTags.FISHES), false));
        this.goalSelector.addGoal(8, new AIWanderIdle());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F){
            @Override
            public boolean canUse() {
                return EntityBaldEagle.this.returnControlTime == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this){
            @Override
            public boolean canUse() {
                return EntityBaldEagle.this.returnControlTime == 0 && super.canUse();
            }
        });
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new AnimalAIHurtByTargetNotBaby(this)));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, LivingEntity.class, 5, true, true,  AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getAllTags().getTag(AMTagRegistry.BALD_EAGLE_TARGETS))) {
            public boolean canUse() {
                return super.canUse() && !EntityBaldEagle.this.isLaunched() && EntityBaldEagle.this.getCommand() == 0;
            }
        });
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.BALD_EAGLE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.BALD_EAGLE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.BALD_EAGLE_HURT;
    }


    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.baldEagleSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }

        return super.isAlliedTo(entityIn);
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new EntityBaldEagle.MoveHelper(this);
            this.navigation = new DirectPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    public boolean save(CompoundTag compound) {
        String s = this.getEncodeId();
        compound.putString("id", s);
        super.save(compound);
        return true;
    }

    public boolean saveAsPassenger(CompoundTag compound) {
        if (!this.isTame()) {
            return super.saveAsPassenger(compound);
        } else {
            String s = this.getEncodeId();
            compound.putString("id", s);
            this.saveWithoutId(compound);
            return true;
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("BirdSitting", this.isSitting());
        compound.putBoolean("Launched", this.isLaunched());
        compound.putBoolean("HasCap", this.hasCap());
        compound.putInt("EagleCommand", this.getCommand());
        compound.putInt("LaunchTime", this.launchTime);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrderedToSit(compound.getBoolean("BirdSitting"));
        this.setLaunched(compound.getBoolean("Launched"));
        this.setCap(compound.getBoolean("HasCap"));
        this.setCommand(compound.getInt("EagleCommand"));
        this.launchTime = compound.getInt("LaunchTime");
    }

    public void travel(Vec3 vec3d) {
        if (!this.shouldHoodedReturn() && this.hasCap() && this.isTame() && !this.isPassenger()) {
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(vec3d);
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.entityData.get(ATTACK_TICK) == 0 && this.attackProgress == 0 && entityIn.isAlive() && this.distanceTo(entityIn) < entityIn.getBbWidth() + 5) {
            this.entityData.set(ATTACK_TICK, 5);
        }
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(HAS_CAP, false);
        this.entityData.define(TACKLING, false);
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(ATTACK_TICK, 0);
        this.entityData.define(COMMAND, Integer.valueOf(0));
        this.entityData.define(SITTING, Boolean.valueOf(false));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isLaunched() {
        return this.entityData.get(LAUNCHED);
    }

    public void setLaunched(boolean flying) {
        this.entityData.set(LAUNCHED, flying);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if(flying && this.isBaby()){
            flying = false;
        }
        this.entityData.set(FLYING, flying);
    }

    public boolean hasCap() {
        return this.entityData.get(HAS_CAP);
    }

    public void setCap(boolean cap) {
        this.entityData.set(HAS_CAP, cap);
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLING);
    }

    public void setTackling(boolean tackling) {
        this.entityData.set(TACKLING, tackling);
    }

    public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
        if (this.distanceTo(owner) > 15) {
            this.setFlying(true);
            this.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ(), followSpeed);
        } else {
            if (this.isFlying() && !this.isOverWaterOrVoid()) {
                BlockPos vec = this.getCrowGround(this.blockPosition());
                if (vec != null) {
                    this.getMoveControl().setWantedPosition(vec.getX(), vec.getY(), vec.getZ(), followSpeed);
                }
                if (this.onGround) {
                    this.setFlying(false);
                }
            } else {
                this.getNavigation().moveTo(owner, followSpeed);
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (item.is(ItemTags.FISHES) && this.getHealth() < this.getMaxHealth()) {
            this.heal(10);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.level.broadcastEntityEvent(this, (byte) 7);
            return InteractionResult.CONSUME;
        } else if (ItemTags.getAllTags().getTag(AMTagRegistry.BALD_EAGLE_TAMEABLES).contains(itemstack.getItem()) && !this.isTame()) {
            if (itemstack.hasContainerItem()) {
                this.spawnAtLocation(itemstack.getContainerItem());
            }
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            if (random.nextBoolean()) {
                this.level.broadcastEntityEvent(this, (byte) 7);
                this.tame(player);
                this.setCommand(1);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.CONSUME;
        } else if (isTame() && !isFood(itemstack)) {
            if (!this.isBaby() && item == AMItemRegistry.FALCONRY_HOOD) {
                if (!this.hasCap()) {
                    this.setCap(true);
                    if (!player.isCreative()) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, this.getSoundVolume(), this.getVoicePitch());
                    return InteractionResult.SUCCESS;
                }
            }else if(item == Items.SHEARS && this.hasCap()) {
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if(!level.isClientSide){
                    if(player instanceof ServerPlayer){
                        itemstack.hurt(1, random, (ServerPlayer)player);
                    }
                }
                this.spawnAtLocation(AMItemRegistry.FALCONRY_HOOD);
                this.setCap(false);
                return InteractionResult.SUCCESS;
            }else if (!this.isBaby() && getRidingEagles(player) <= 0 && (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE)) {
                boardingCooldown = 30;
                this.setLaunched(false);
                this.ejectPassengers();
                this.startRiding(player, true);
                if (!level.isClientSide) {
                    AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.getId(), player.getId()));
                }
                return InteractionResult.SUCCESS;
            } else {
                this.setCommand((this.getCommand() + 1) % 3);
                if (this.getCommand() == 3) {
                    this.setCommand(0);
                }
                player.displayClientMessage(new TranslatableComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.setOrderedToSit(true);
                    return InteractionResult.SUCCESS;
                } else {
                    this.setOrderedToSit(false);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return type;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1 && !isLaunched();
    }

    public int getRidingEagles(LivingEntity player) {
        int crowCount = 0;
        for (Entity e : player.getPassengers()) {
            if (e instanceof EntityBaldEagle) {
                crowCount++;
            }
        }
        return crowCount;
    }

    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && (!entity.isAlive() || !this.isAlive())) {
            this.stopRiding();
        } else if (isTame() && entity instanceof LivingEntity && isOwnedBy((LivingEntity) entity)) {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                Entity mount = this.getVehicle();
                if (mount instanceof Player) {
                    float yawAdd = 0;
                    if (((Player) mount).getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                        yawAdd = ((Player) mount).getMainArm() == HumanoidArm.LEFT ? 135 : -135;
                    } else if (((Player) mount).getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                        yawAdd = ((Player) mount).getMainArm() == HumanoidArm.LEFT ? -135 : 135;
                    } else {
                        this.setCommand(2);
                        this.setOrderedToSit(true);
                        this.removeVehicle();
                        this.copyPosition(mount);
                    }
                    float birdYaw = yawAdd * 0.5F;
                    this.yBodyRot = Mth.wrapDegrees(((LivingEntity) mount).yBodyRot + birdYaw);
                    this.yRot = Mth.wrapDegrees(((LivingEntity) mount).yRot + birdYaw);
                    this.yHeadRot = Mth.wrapDegrees(((LivingEntity) mount).yHeadRot + birdYaw);
                    float radius = 0.6F;
                    float angle = (0.01745329251F * (((LivingEntity) mount).yBodyRot - 180F + yawAdd));
                    double extraX = radius * Mth.sin((float) (Math.PI + angle));
                    double extraZ = radius * Mth.cos(angle);
                    this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getBbHeight() * 0.45F, mount.getY()), mount.getZ() + extraZ);
                }
                if (!mount.isAlive()) {
                    this.removeVehicle();
                }
            }
        } else {
            super.rideTick();
        }
    }

    public void tick() {
        super.tick();

        this.prevAttackProgress = attackProgress;
        this.prevBirdPitch = birdPitch;
        this.prevTackleProgress = tackleProgress;
        this.prevFlyProgress = flyProgress;
        this.prevFlapAmount = flapAmount;
        this.prevSwoopProgress = swoopProgress;
        this.prevSitProgress = sitProgress;
        float yMot = (float) -((float) this.getDeltaMovement().y * (double) (180F / (float) Math.PI));
        this.birdPitch = yMot;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (isTackling() && tackleProgress < 5F) {
            tackleProgress++;
        }
        if (!isTackling() && tackleProgress > 0F) {
            tackleProgress--;
        }
        boolean sit = isSitting() || this.isPassenger();
        if (sit && sitProgress < 5F) {
            sitProgress++;
        }
        if (!sit && sitProgress > 0F) {
            sitProgress--;
        }
        if (this.isLaunched()) {
            launchTime++;
        } else {
            launchTime = 0;
        }
        if (lastPlayerControlTime > 0) {
            lastPlayerControlTime--;
        }
        if (lastPlayerControlTime <= 0) {
            controlledFlag = false;
        }
        if (yMot < 0.1F) {
            flapAmount = Math.min(-yMot * 0.2F, 1F);
            if (swoopProgress > 0) {
                swoopProgress--;
            }
        } else {
            if (flapAmount > 0.0F) {
                flapAmount -= Math.min(flapAmount, 0.1F);
            } else {
                flapAmount = 0;
            }
            if (swoopProgress < yMot * 0.2F) {
                swoopProgress = Math.min(yMot * 0.2F, swoopProgress + 1);
            }
        }
        if (this.isTackling()) {
            flapAmount = Math.min(2, flapAmount + 0.2F);
        }
        if (!level.isClientSide) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (this.isTackling() && !this.isVehicle() && (this.getTarget() == null || !this.getTarget().isAlive()) && tackleCapCooldown == 0) {
                this.setTackling(false);
            }
            if (isFlying()) {
                timeFlying++;
                this.setNoGravity(true);
                if (this.isSitting() || this.isPassenger() || this.isInLove()) {
                    if(!isLaunched()){
                        this.setFlying(false);
                    }
                }
                if (this.getTarget() != null && this.getTarget().getY() < this.getX() && !this.isVehicle()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.9, 1.0));
                }
            } else {
                timeFlying = 0;
                this.setNoGravity(false);
            }
            if (this.isInWaterOrBubble() && this.isVehicle()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1F, 0));
            }
            if(this.isSitting() && !this.isLaunched()){
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1F, 0));
            }
            if (this.getTarget() != null && this.isInWaterOrBubble()) {
                timeFlying = 0;
                this.setFlying(true);
            }
            if (isFlying() && this.onGround && !this.isInWaterOrBubble() && this.timeFlying > 30) {
                this.setFlying(false);
            }
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            if (this.entityData.get(ATTACK_TICK) == 2 && this.getTarget() != null && this.distanceTo(this.getTarget()) < this.getTarget().getBbWidth() + 2D) {
                this.getTarget().hurt(DamageSource.mobAttack(this), 2);
            }
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }
        if (this.isPassenger()) {
            this.setFlying(false);
            this.setTackling(false);
        }
        if (boardingCooldown > 0) {
            boardingCooldown--;
        }
        if (returnControlTime > 0) {
            returnControlTime--;
        }
        if (tackleCapCooldown > 0) {
            tackleCapCooldown--;
        }
    }

    @Nullable
    @Override
    public AgableMob getBreedOffspring(ServerLevel p_241840_1_, AgableMob p_241840_2_) {
        return AMEntityRegistry.BALD_EAGLE.create(p_241840_1_);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24) - radiusAdd;
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, 0, fleePos.z() + extraZ);
        BlockPos ground = getCrowGround(radialPos);
        int distFromGround = (int) this.getY() - ground.getY();
        int flightHeight = 7 + this.getRandom().nextInt(10);
        BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : this.getRandom().nextInt(7) + 4);
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }

    private BlockPos getCrowGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getY(), in.getZ());
        while (position.getY() < 256 && !level.getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > 2 && level.isEmptyBlock(position)) {
            position = position.below();
        }
        return position;
    }

    public Vec3 getBlockGrounding(Vec3 fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getCrowGround(radialPos);
        if (ground.getY() == 0) {
            return this.position();
        } else {
            ground = this.blockPosition();
            while (ground.getY() > 2 && level.isEmptyBlock(ground)) {
                ground = ground.below();
            }
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return Vec3.atCenterOf(ground);
        }
        return null;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());

        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private Vec3 getOrbitVec(Vec3 vector3d, float gatheringCircleDist) {
        float angle = (0.01745329251F * (float) this.orbitDist * (orbitClockwise ? -tickCount : tickCount));
        double extraX = gatheringCircleDist * Mth.sin((angle));
        double extraZ = gatheringCircleDist * Mth.cos(angle);
        if (this.orbitPos != null) {
            Vec3 pos = new Vec3(orbitPos.getX() + extraX, orbitPos.getY() + random.nextInt(2) - 2, orbitPos.getZ() + extraZ);
            if (this.level.isEmptyBlock(new BlockPos(pos))) {
                return pos;
            }
        }
        return null;
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > 0 && level.isEmptyBlock(position)) {
            position = position.below();
        }
        return !level.getFluidState(position).isEmpty() || position.getY() <= 0;
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float radius = 0.3F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            passenger.yRot = this.yBodyRot + 90F;
            if (passenger instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) passenger;
                living.yBodyRot = this.yBodyRot + 90F;
            }
            double extraY = 0;
            if (passenger instanceof AbstractFish && !passenger.isInWaterOrBubble()) {
                extraY = 0.1F;
            }
            passenger.setPos(this.getX() + extraX, this.getY() - 0.3F + extraY + passenger.getBbHeight() * 0.3F, this.getZ() + extraZ);
            passengerTimer++;
            if (this.isAlive() && passengerTimer > 0 && passengerTimer % 40 == 0) {
                passenger.hurt(DamageSource.mobAttack(this), 1);
            }
        }
    }

    public boolean canBeRiddenInWater(Entity rider) {
        return true;
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    public boolean shouldHoodedReturn() {
        if (this.getOwner() != null) {
            if (!this.getOwner().isAlive() || this.getOwner().isShiftKeyDown() ) {
                return true;
            }
        }
        return !this.isAlive() || launchTime > 12000 || this.isInsidePortal || this.portalTime > 0 || removed;
    }

    public void remove(boolean keepData) {
        if (this.lastPlayerControlTime == 0 && !this.isPassenger()) {
            super.remove(keepData);
        }
    }

    public void directFromPlayer(float rotationYaw, float rotationPitch, boolean loadChunk, Entity over) {
        Entity owner = this.getOwner();
        if (owner != null && this.distanceTo(owner) > 150) {
            returnControlTime = 100;
        }
        if(Math.abs(xo - this.getX()) > 0.1F || Math.abs(yo - this.getY()) > 0.1F || Math.abs(zo - this.getZ()) > 0.1F){
            stillTicksCounter = 0;
        }else{
            stillTicksCounter++;
        }
        int stillTPthreshold = AMConfig.falconryTeleportsBack ? 200 : 6000;
        this.setOrderedToSit(false);
        this.setLaunched(true);
        if((returnControlTime > 0 && AMConfig.falconryTeleportsBack || stillTicksCounter > stillTPthreshold && this.distanceTo(owner) > 30) && owner != null){
            this.copyPosition(owner);
            returnControlTime = 0;
            stillTicksCounter = 0;
            launchTime = Math.max(launchTime, 12000);
        }
        if (!level.isClientSide) {
            if (returnControlTime > 0 && owner != null) {
                double d0 = this.getX() - owner.getX();
                double d2 = this.getZ() - owner.getZ();
                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) + 90.0F;
                this.getLookControl().setLookAt(owner, 30, 30);
            } else {
                this.yBodyRot = rotationYaw;
                this.yRot = rotationYaw;
                this.yHeadRot = rotationYaw;
                this.xRot = rotationPitch;
            }
            if (rotationPitch < 10 && this.isOnGround()) {
                this.setFlying(true);
            }
            float yawOffset = rotationYaw + 90;
            float rad = 3F;
            float speed = 1.2F;
            if (returnControlTime > 0) {
                this.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + 10, owner.getZ(), speed);
            } else {
                this.getMoveControl().setWantedPosition(this.getX() + rad * 1.5F * Math.cos(yawOffset * (Math.PI / 180.0F)), this.getY() - rad * Math.sin(rotationPitch * (Math.PI / 180.0F)), this.getZ() + rad * Math.sin(yawOffset * (Math.PI / 180.0F)), speed);
            }
            if (loadChunk) {
                loadChunkOnServer(this.blockPosition());
            }
            this.setLastHurtByMob(null);
            this.setTarget(null);
            if (over == null) {
                List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(3.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
                Entity closest = null;
                for (Entity e : list) {
                    if (closest == null || this.distanceTo(e) < this.distanceTo(closest)) {
                        closest = e;
                    }
                }
                over = closest;
            }
        }
        if (over != null && !this.isAlliedTo(over) && over != owner && canFalconryAttack(over)) {
            if (tackleCapCooldown == 0 && this.distanceTo(over) <= over.getBbWidth() + 4D) {
                this.setTackling(true);
                if (this.distanceTo(over) <= over.getBbWidth() + 2D) {
                    float speedDamage = (float) Math.ceil(Mth.clamp(this.getDeltaMovement().length() + 0.2, 0, 1.2D) * 3.333);
                    over.hurt(DamageSource.mobAttack(this), 5 + speedDamage + random.nextInt(2));
                    tackleCapCooldown = 22;
                }
            }
        }
        this.lastPlayerControlTime = 10;
        this.controlledFlag = true;
    }

    private boolean canFalconryAttack(Entity over) {
        return !(over instanceof ItemEntity) && (!(over instanceof LivingEntity) || !this.isOwnedBy((LivingEntity)over));
    }

    //killEntity
    public void killed(ServerLevel world, LivingEntity entity) {
        if (this.isLaunched() && this.hasCap() && this.isTame() && this.getOwner() != null) {
            if (this.getOwner() instanceof ServerPlayer && this.distanceTo(this.getOwner()) >= 100) {
                AMAdvancementTriggerRegistry.BALD_EAGLE_CHALLENGE.trigger((ServerPlayer) this.getOwner());
            }
        }
        super.killed(world, entity);
    }


    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow) && this.isLaunched()) {
                amount = (amount + 1.0F) / 4.0F;
            }
            return super.hurt(source, amount);
        }
    }

    public void loadChunkOnServer(BlockPos center) {
        if (!this.level.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) level;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    ChunkPos pos = new ChunkPos(this.blockPosition().offset(i * 16, 0, j * 16));
                    serverWorld.setChunkForced(pos.x, pos.z, true);

                }
            }
        }
    }

    class MoveHelper extends MoveControl {
        private final EntityBaldEagle parentEntity;

        public MoveHelper(EntityBaldEagle bird) {
            super(bird);
            this.parentEntity = bird;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d5 = vector3d.length();
                if (d5 < 0.3) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    double d0 = this.wantedX - this.parentEntity.getX();
                    double d1 = this.wantedY - this.parentEntity.getY();
                    double d2 = this.wantedZ - this.parentEntity.getZ();
                    double d3 = Mth.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d5)));
                    Vec3 vector3d1 = parentEntity.getDeltaMovement();
                    parentEntity.yRot = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    parentEntity.yBodyRot = parentEntity.yRot;

                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.parentEntity.level.noCollision(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    private class AIWanderIdle extends Goal {
        protected final EntityBaldEagle eagle;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;
        private int orbitResetCooldown = 0;
        private int maxOrbitTime = 360;
        private int orbitTime = 0;

        public AIWanderIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.eagle = EntityBaldEagle.this;
        }

        @Override
        public boolean canUse() {
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if ((eagle.getTarget() != null && eagle.getTarget().isAlive() && !this.eagle.isVehicle()) || this.eagle.isPassenger() || this.eagle.isSitting() || eagle.controlledFlag) {
                return false;
            } else {
                if (this.eagle.getRandom().nextInt(15) != 0 && !eagle.isFlying()) {
                    return false;
                }
                if(this.eagle.isBaby()){
                    this.flightTarget = false;
                }else if (this.eagle.isInWaterOrBubble()) {
                    this.flightTarget = true;
                } else if (this.eagle.hasCap()) {
                    this.flightTarget = false;
                } else if (this.eagle.isOnGround()) {
                    this.flightTarget = random.nextBoolean();
                } else {
                    if (orbitResetCooldown == 0 && random.nextInt(6) == 0) {
                        orbitResetCooldown = 400;
                        eagle.orbitPos = eagle.blockPosition();
                        eagle.orbitDist = 4 + random.nextInt(5);
                        eagle.orbitClockwise = random.nextBoolean();
                        orbitTime = 0;
                        maxOrbitTime = (int) (360 + 360 * random.nextFloat());
                    }
                    this.flightTarget = eagle.isVehicle() || random.nextInt(7) > 0 && eagle.timeFlying < 700;
                }
                Vec3 lvt_1_1_ = this.getPosition();
                if (lvt_1_1_ == null) {
                    return false;
                } else {
                    this.x = lvt_1_1_.x;
                    this.y = lvt_1_1_.y;
                    this.z = lvt_1_1_.z;
                    return true;
                }
            }
        }

        public void tick() {
            if (orbitResetCooldown > 0) {
                orbitResetCooldown--;
            }
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if (orbitResetCooldown > 0 && eagle.orbitPos != null) {
                if (orbitTime < maxOrbitTime && !eagle.isInWaterOrBubble()) {
                    orbitTime++;
                } else {
                    orbitTime = 0;
                    eagle.orbitPos = null;
                    orbitResetCooldown = -400 - random.nextInt(400);
                }
            }
            if (eagle.horizontalCollision && !eagle.onGround) {
                stop();
            }
            if (flightTarget) {
                eagle.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                if (eagle.isFlying() && !eagle.onGround) {
                    if (!eagle.isInWaterOrBubble()) {
                        eagle.setDeltaMovement(eagle.getDeltaMovement().multiply(1.2F, 0.6F, 1.2F));
                    }
                } else {
                    this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1F);
                }
            }
            if (!flightTarget && isFlying() && eagle.onGround) {
                eagle.setFlying(false);
                orbitTime = 0;
                eagle.orbitPos = null;
                orbitResetCooldown = -400 - random.nextInt(400);
            }
            if (isFlying() && (!level.isEmptyBlock(eagle.getBlockPosBelowThatAffectsMyMovement()) || eagle.onGround) && !eagle.isInWaterOrBubble() && eagle.timeFlying > 30) {
                eagle.setFlying(false);
                orbitTime = 0;
                eagle.orbitPos = null;
                orbitResetCooldown = -400 - random.nextInt(400);
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = eagle.position();
            if (eagle.isTame() && eagle.getCommand() == 1 && eagle.getOwner() != null) {
                vector3d = eagle.getOwner().position();
                eagle.orbitPos = eagle.getOwner().blockPosition();
            }
            if (orbitResetCooldown > 0 && eagle.orbitPos != null) {
                return eagle.getOrbitVec(vector3d, 4 + random.nextInt(2));
            }
            if (eagle.isVehicle() || eagle.isOverWaterOrVoid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (eagle.timeFlying < 500 || eagle.isVehicle() || eagle.isOverWaterOrVoid()) {
                    return eagle.getBlockInViewAway(vector3d, 0);
                } else {
                    return eagle.getBlockGrounding(vector3d);
                }
            } else {
                return RandomPos.getPos(this.eagle, 10, 7);
            }
        }

        public boolean canContinueToUse() {
            if (eagle.isSitting()) {
                return false;
            }
            if (flightTarget) {
                return eagle.isFlying() && eagle.distanceToSqr(x, y, z) > 2F;
            } else {
                return (!this.eagle.getNavigation().isDone()) && !this.eagle.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                eagle.setFlying(true);
                eagle.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.eagle.getNavigation().stop();
            super.stop();
        }
    }

    private class AITackle extends Goal {
        protected EntityBaldEagle eagle;
        private int circleTime;
        private int maxCircleTime = 10;


        public AITackle() {
            this.eagle = EntityBaldEagle.this;
        }

        @Override
        public boolean canUse() {
            return eagle.getTarget() != null && !eagle.controlledFlag && !eagle.isVehicle();
        }

        public void start() {
            eagle.orbitPos = null;
        }

        public void stop() {
            circleTime = 0;
            maxCircleTime = 60 + random.nextInt(60);
        }

        public void tick() {
            LivingEntity target = eagle.getTarget();
            boolean smallPrey = target != null && target.getBbHeight() < 1F && target.getBbWidth() < 0.7F && !(target instanceof EntityBaldEagle) || target instanceof AbstractFish;
            if (eagle.orbitPos != null && circleTime < maxCircleTime) {
                circleTime++;
                eagle.setTackling(false);
                eagle.setFlying(true);
                if (target != null) {
                    int i = 0;
                    int up = 2 + eagle.getRandom().nextInt(4);
                    eagle.orbitPos = target.blockPosition().above((int) (target.getBbHeight()));
                    while (eagle.level.isEmptyBlock(eagle.orbitPos) && i < up) {
                        i++;
                        eagle.orbitPos = eagle.orbitPos.above();
                    }
                }
                Vec3 vec = eagle.getOrbitVec(Vec3.ZERO, 4 + random.nextInt(2));
                if (vec != null) {
                    eagle.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 1.2F);
                }
            } else if (target != null) {
                if (eagle.isFlying() || eagle.isInWaterOrBubble()) {
                    double d0 = eagle.getX() - target.getX();
                    double d2 = eagle.getZ() - target.getZ();
                    double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
                    double yAddition = target.getBbHeight();
                    if (xzDist > 15) {
                        yAddition = 3D;
                    }
                    eagle.setTackling(true);
                    eagle.getMoveControl().setWantedPosition(target.getX(), target.getY() + yAddition, target.getZ(), eagle.isInWaterOrBubble() ? 1.3F : 1.0F);
                } else {
                    this.eagle.getNavigation().moveTo(target, 1F);
                }
                if (eagle.distanceTo(target) < target.getBbWidth() + 2.5F) {
                    if (eagle.isTackling()) {
                        if (smallPrey) {
                            eagle.setFlying(true);
                            eagle.timeFlying = 0;
                            float radius = 0.3F;
                            float angle = (0.01745329251F * eagle.yBodyRot);
                            double extraX = radius * Mth.sin((float) (Math.PI + angle));
                            double extraZ = radius * Mth.cos(angle);
                            target.yRot = eagle.yBodyRot + 90F;
                            if (target instanceof LivingEntity) {
                                LivingEntity living = target;
                                living.yBodyRot = eagle.yBodyRot + 90F;
                            }
                            target.setPos(eagle.getX() + extraX, eagle.getY() - 0.4F + target.getBbHeight() * 0.45F, eagle.getZ() + extraZ);
                            target.startRiding(eagle, true);
                        } else {
                            target.hurt(DamageSource.mobAttack(eagle), 5);
                            eagle.setFlying(false);
                            eagle.orbitPos = target.blockPosition().above(2);
                            circleTime = 0;
                            maxCircleTime = 60 + random.nextInt(60);
                        }
                    } else {
                        eagle.doHurtTarget(target);
                    }
                } else if (eagle.distanceTo(target) > 12 || target.isInWaterOrBubble()) {
                    eagle.setFlying(true);
                }
            }
            if (eagle.isLaunched()) {
                eagle.setFlying(true);
            }
        }
    }

    private class AILandOnGlove extends Goal {
        protected EntityBaldEagle eagle;
        private int seperateTime = 0;

        public AILandOnGlove() {
            this.eagle = EntityBaldEagle.this;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return eagle.isLaunched() && !eagle.controlledFlag && eagle.isTame() && !eagle.isPassenger() && !eagle.isVehicle() && (eagle.getTarget() == null || !eagle.getTarget().isAlive());
        }

        public void tick() {
            if (eagle.getDeltaMovement().lengthSqr() < 0.03D) {
                seperateTime++;
            }
            LivingEntity owner = eagle.getOwner();
            if (owner != null) {
                if (seperateTime > 200) {
                    seperateTime = 0;
                    eagle.copyPosition(owner);
                }
                eagle.setFlying(true);
                double d0 = eagle.getX() - owner.getX();
                double d2 = eagle.getZ() - owner.getZ();
                double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
                double yAdd = xzDist > 14 ? 5 : 0;
                eagle.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + yAdd + owner.getEyeHeight(), owner.getZ(), 1);

                if (this.eagle.distanceTo(owner) < owner.getBbWidth() + 1.4D) {
                    this.eagle.setLaunched(false);
                    if (this.eagle.getRidingEagles(owner) <= 0) {
                        this.eagle.startRiding(owner);
                        if (!eagle.level.isClientSide) {
                            AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(eagle.getId(), owner.getId()));
                        }
                    } else {
                        this.eagle.setCommand(2);
                        this.eagle.setOrderedToSit(true);
                    }
                }
            }
        }

        public void stop() {
            seperateTime = 0;
        }
    }
}
