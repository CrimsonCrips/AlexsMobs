package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.client.model.ModelFarseer;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderNameTagEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderFarseer extends EntityRenderer<EntityFarseer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer.png");
    private static final ResourceLocation[] PORTAL_TEXTURES = new ResourceLocation[]{
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_0.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_1.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_2.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_3.png")};

    public static final ModelFarseer MODEL_FARSEER = new ModelFarseer(1);



    public RenderFarseer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    public ResourceLocation getTextureLocation(EntityFarseer entity) {
        return TEXTURE;
    }

    public void render(EntityFarseer entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        matrixStackIn.pushPose();

        matrixStackIn.pushPose();
        matrixStackIn.scale(3.0F, 3.0F, 3.0F);
        PoseStack.Pose posestack$pose = matrixStackIn.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        int portalTexture = Mth.clamp(entityIn.getPortalFrame(), 0, PORTAL_TEXTURES.length - 1);
        VertexConsumer portalStatic = AMRenderTypes.createMergedVertexConsumer(bufferIn.getBuffer(AMRenderTypes.STATIC_PORTAL), bufferIn.getBuffer(RenderType.entityTranslucent(PORTAL_TEXTURES[portalTexture])));
        float portalAlpha =  entityIn.getPortalOpacity(partialTicks);
        portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1, portalAlpha);
        portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1, portalAlpha);
        portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0, portalAlpha);
        portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0, portalAlpha);
        matrixStackIn.popPose();
        MODEL_FARSEER.setupAnim(entityIn,0,0,partialTicks,0,0);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(MODEL_FARSEER.renderType(TEXTURE));
        MODEL_FARSEER.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();



    }

    private static void portalVertex(VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, int p_114095_, int p_114096_, int p_114097_, float alpha) {
        p_114090_.vertex(p_114091_, p_114094_ - 0.5F, (float)p_114095_ - 0.25F, 0.0F).color(1F, 1F, 1F,  alpha).uv((float)p_114096_, (float)p_114097_).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }


}
