package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.message.MessageSendVisualFlagFromServer;
import com.github.alexthe666.alexsmobs.misc.AMDamageTypes;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EntityFarseer extends Entity implements IAnimatedEntity {

    public static final Animation ANIMATION_EMERGE = Animation.create(60);
    private static final EntityDataAccessor<Boolean> HAS_EMERGED = SynchedEntityData.defineId(EntityFarseer.class, EntityDataSerializers.BOOLEAN);
    private int animationTick;
    private Animation currentAnimation;

    protected EntityFarseer(EntityType<? extends Entity> type, Level level) {
        super(type, level);
    }

    public EntityFarseer(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.FARSEER.get(), world);
    }


    public boolean isNoGravity() {
        return true;
    }



    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Emerged", this.hasEmerged());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        this.setHasEmerged(compound.getBoolean("Emerged"));
    }

    protected void defineSynchedData() {
        this.entityData.define(HAS_EMERGED, false);
    }

    public boolean hasEmerged() {
        return this.entityData.get(HAS_EMERGED);
    }

    public void setHasEmerged(boolean emerged) {
        this.entityData.set(HAS_EMERGED, Boolean.valueOf(emerged));
    }

    public void tick() {

        if (!this.hasEmerged()) {
            this.setAnimation(ANIMATION_EMERGE);
            this.setHasEmerged(true);
        }

        if (getAnimation() == ANIMATION_EMERGE){
            if (this.hasEmerged() && this.getAnimationTick() < 50){
                this.setAnimationTick(getAnimationTick() + 1);
            } else if (this.getAnimationTick() >= 50){
                this.setInvisible(true);
            }

            Level level = this.level();
            if(level.isClientSide){
                level.addParticle(AMParticleRegistry.STATIC_SPARK.get(), this.getRandomX(0.75F), this.getRandomY(), this.getRandomZ(0.75F), (level.getRandom().nextFloat() - 0.5F) * 0.2F, level.getRandom().nextFloat() * 0.2F, (level.getRandom().nextFloat() - 0.5F) * 0.2F);
            }
            if(this.getAnimationTick() == 1){
                this.playSound(AMSoundRegistry.FARSEER_EMERGE.get(), 1, 1);
            }
        }




    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        this.currentAnimation = animation;
    }


    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_EMERGE};
    }

    public int getPortalFrame() {
        if (this.getAnimation() == ANIMATION_EMERGE) {
            if (this.getAnimationTick() < 10) {
                return 0;
            } else if (this.getAnimationTick() < 20) {
                return 1;
            } else if (this.getAnimationTick() < 30) {
                return 2;
            } else if (this.getAnimationTick() > 40 && hasEmerged()) {
                return 40;
            }
        }
        int i = 50 - this.getAnimationTick();
        return i < 6 ? i < 3 ? 0 : 1 : 2;
    }



    public float getPortalOpacity(float partialTicks) {
        if (this.getAnimation() == ANIMATION_EMERGE) {
            float tick = this.getAnimationTick() - 1 + partialTicks;
            if (tick < 5F) {
                return tick / 5F;
            }
            return 1.0F;
        }
        return 1.0F;
    }

}
