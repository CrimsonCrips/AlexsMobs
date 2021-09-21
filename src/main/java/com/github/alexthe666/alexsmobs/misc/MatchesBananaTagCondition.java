package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.CommonProxy;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.loot.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;

import java.util.Set;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class MatchesBananaTagCondition implements LootItemCondition {

    private Tag<Block> match;

    private MatchesBananaTagCondition() {
        match = BlockTags.getAllTags().getTag(AMTagRegistry.DROPS_BANANAS);
    }

    public LootItemConditionType getType() {
        return CommonProxy.MATCHES_BANANA_CONDTN;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    public boolean test(LootContext p_test_1_) {
        if(match == null){
            match = BlockTags.getAllTags().getTag(AMTagRegistry.DROPS_BANANAS);
        }
        BlockState block = p_test_1_.getParamOrNull(LootContextParams.BLOCK_STATE);
        return block != null && match != null && match.contains(block.getBlock());
    }

    public static class Serializer implements Serializer<MatchesBananaTagCondition> {
        public Serializer() {
        }

        public void serialize(JsonObject p_230424_1_, MatchesBananaTagCondition p_230424_2_, JsonSerializationContext p_230424_3_) {
        }

        public MatchesBananaTagCondition deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            return new MatchesBananaTagCondition();
        }
    }
}
