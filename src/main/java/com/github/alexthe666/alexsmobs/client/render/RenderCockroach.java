package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCockroachMaracas;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCockroachRainbow;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCockroach extends MobRenderer<EntityCockroach, ModelCockroach> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cockroach.png");

    public RenderCockroach(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelCockroach(), 0.3F);
        this.addLayer(new LayerCockroachRainbow(this));
        this.addLayer(new LayerCockroachMaracas(this));
    }

    protected void scale(EntityCockroach entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }


    public ResourceLocation getTextureLocation(EntityCockroach entity) {
        return TEXTURE;
    }
}
