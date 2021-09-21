package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityCachalotEcho;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.base.Predicates;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.debug.CaveDebugRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.placement.CaveEdge;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemEcholocator extends Item {

    public boolean ender;

    public ItemEcholocator(Item.Properties properties, boolean ender) {
        super(properties);
        this.ender = ender;
    }

    private List<BlockPos> getNearbyPortals(BlockPos blockpos, ServerLevel world, int range) {
        if(ender){
            PoiManager pointofinterestmanager = world.getPoiManager();
            Stream<BlockPos> stream = pointofinterestmanager.findAll(AMPointOfInterestRegistry.END_PORTAL_FRAME.getPredicate(), Predicates.alwaysTrue(), blockpos, range, PoiManager.Occupancy.ANY);
            return stream.collect(Collectors.toList());
        }else{
            Random random = new Random();
            for(int i = 0; i < 256; i++){
                BlockPos checkPos = blockpos.offset(random.nextInt(range) - range/2, random.nextInt(range)/2 - range/2, random.nextInt(range) - range/2);
                if(world.getBlockState(checkPos).getBlock() == Blocks.CAVE_AIR && world.getMaxLocalRawBrightness(checkPos) < 4){
                    return Collections.singletonList(checkPos);
                }
            }
            return Collections.emptyList();
        }

    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player livingEntityIn, InteractionHand handIn) {
        ItemStack stack = livingEntityIn.getItemInHand(handIn);
        boolean left = false;
        if (livingEntityIn.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntityIn.getMainArm() == HumanoidArm.RIGHT || livingEntityIn.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntityIn.getMainArm() == HumanoidArm.LEFT) {
            left = true;
        }
        EntityCachalotEcho whaleEcho = new EntityCachalotEcho(worldIn, livingEntityIn, !left);
        if (!worldIn.isClientSide && worldIn instanceof ServerLevel) {
            BlockPos playerPos = livingEntityIn.blockPosition();
            List<BlockPos> portals = getNearbyPortals(playerPos, (ServerLevel) worldIn, 128);
            BlockPos pos = null;
            if(ender){
                for (BlockPos portalPos : portals) {
                    if (pos == null || pos.distSqr(playerPos) > portalPos.distSqr(playerPos)) {
                        pos = portalPos;
                    }
                }
            }else{
                CompoundTag nbt = stack.getOrCreateTag();
                if(nbt.contains("CavePos") && nbt.getBoolean("ValidCavePos")){
                    pos = BlockPos.of(nbt.getLong("CavePos"));
                    if(worldIn.getBlockState(pos).getBlock() != Blocks.CAVE_AIR ||worldIn.getMaxLocalRawBrightness(pos) >= 4 || 1000000 < pos.distSqr(playerPos)){
                        nbt.putBoolean("ValidCavePos", false);
                    }
                }else{
                    for (BlockPos portalPos : portals) {
                        if (pos == null || pos.distSqr(playerPos) < portalPos.distSqr(playerPos)) {
                            pos = portalPos;
                        }
                    }
                    if(pos != null){
                        nbt.putLong("CavePos", pos.asLong());
                        nbt.putBoolean("ValidCavePos", true);
                        stack.setTag(nbt);
                    }
                }

            }
            if (pos != null) {
                double d0 = pos.getX() + 0.5F - whaleEcho.getX();
                double d1 = pos.getY() + 0.5F - whaleEcho.getY();
                double d2 = pos.getZ() + 0.5F - whaleEcho.getZ();
                whaleEcho.tickCount = 15;
                whaleEcho.shoot(d0, d1, d2, 0.4F, 0.3F);
                worldIn.addFreshEntity(whaleEcho);
                worldIn.playSound((Player)null, whaleEcho.getX(), whaleEcho.getY(), whaleEcho.getZ(), AMSoundRegistry.CACHALOT_WHALE_CLICK, SoundSource.PLAYERS, 1.0F, 1.0F);
                stack.hurtAndBreak(1, livingEntityIn, (player) -> {
                    player.broadcastBreakEvent(livingEntityIn.getUsedItemHand());
                });
            }
        }
        livingEntityIn.getCooldowns().addCooldown(this, 5);

        return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide());
    }
}
