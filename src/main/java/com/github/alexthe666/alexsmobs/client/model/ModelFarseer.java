package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ModelFarseer extends AdvancedEntityModel<EntityFarseer> {
    private final AdvancedModelBox root;

    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox leftElbow;
    private final AdvancedModelBox leftHand;
    private final AdvancedModelBox leftUpperRFinger;
    private final AdvancedModelBox leftLowerRFinger;
    private final AdvancedModelBox leftLowerLFinger;
    private final AdvancedModelBox leftUpperLFinger;
    private final AdvancedModelBox leftArm2;
    private final AdvancedModelBox leftElbow2;
    private final AdvancedModelBox leftHand2;
    private final AdvancedModelBox leftUpperRFinger2;
    private final AdvancedModelBox leftLowerRFinger2;
    private final AdvancedModelBox leftLowerLFinger2;
    private final AdvancedModelBox leftUpperLFinger2;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox rightElbow;
    private final AdvancedModelBox rightHand;
    private final AdvancedModelBox rightUpperRFinger;
    private final AdvancedModelBox rightLowerRFinger2;
    private final AdvancedModelBox rightLowerLFinger;
    private final AdvancedModelBox rightUpperLFinger;
    private final AdvancedModelBox rightArm2;
    private final AdvancedModelBox rightElbow2;
    private final AdvancedModelBox rightHand2;
    private final AdvancedModelBox rightUpperRFinger2;
    private final AdvancedModelBox rightLowerRFinger3;
    private final AdvancedModelBox rightLowerLFinger2;
    private final AdvancedModelBox rightUpperLFinger2;

    private final ModelAnimator animator;

    public ModelFarseer(float scale) {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        leftArm = new AdvancedModelBox(this, "leftArm");
        leftArm.setRotationPoint(9.0F, -16.5F, 9.0F);
        root.addChild(leftArm);
        setRotationAngle(leftArm, 0.0F, 0.0F, -0.7854F);
        leftArm.setTextureOffset(31, 23).addBox(-1.0F, -1.5F, -2.0F, 16.0F, 3.0F, 4.0F, scale, false);

        leftElbow = new AdvancedModelBox(this, "leftElbow");
        leftElbow.setRotationPoint(15.0F, 0.0F, 0.0F);
        leftArm.addChild(leftElbow);
        leftElbow.setTextureOffset(0, 39).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 14.0F, scale, false);

        leftHand = new AdvancedModelBox(this, "leftHand");
        leftHand.setRotationPoint(0.0F, 0.0F, -12.0F);
        leftElbow.addChild(leftHand);


        leftUpperRFinger = new AdvancedModelBox(this, "leftUpperRFinger");
        leftUpperRFinger.setRotationPoint(-1.4F, -1.4F, 0.0F);
        leftHand.addChild(leftUpperRFinger);
        setRotationAngle(leftUpperRFinger, 0.0F, 0.0F, -0.7854F);
        leftUpperRFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, false);

        leftLowerRFinger = new AdvancedModelBox(this, "leftLowerRFinger");
        leftLowerRFinger.setRotationPoint(-1.4F, 1.4F, 0.0F);
        leftHand.addChild(leftLowerRFinger);
        setRotationAngle(leftLowerRFinger, 0.0F, 0.0F, -2.3562F);
        leftLowerRFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, false);

        leftLowerLFinger = new AdvancedModelBox(this, "leftLowerLFinger");
        leftLowerLFinger.setRotationPoint(1.4F, 1.4F, 0.0F);
        leftHand.addChild(leftLowerLFinger);
        setRotationAngle(leftLowerLFinger, 0.0F, 0.0F, 2.3562F);
        leftLowerLFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, false);

        leftUpperLFinger = new AdvancedModelBox(this, "leftUpperLFinger");
        leftUpperLFinger.setRotationPoint(1.4F, -1.4F, 0.0F);
        leftHand.addChild(leftUpperLFinger);
        setRotationAngle(leftUpperLFinger, 0.0F, 0.0F, 0.7854F);
        leftUpperLFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, false);

        leftArm2 = new AdvancedModelBox(this, "leftArm2");
        leftArm2.setRotationPoint(6.0F, -13.5F, 9.0F);
        root.addChild(leftArm2);
        setRotationAngle(leftArm2, 0.0F, 0.0F, 0.6545F);
        leftArm2.setTextureOffset(31, 23).addBox(-1.0F, -1.5F, -2.0F, 16.0F, 3.0F, 4.0F, scale, false);

        leftElbow2 = new AdvancedModelBox(this, "leftElbow2");
        leftElbow2.setRotationPoint(15.0F, 0.0F, 0.0F);
        leftArm2.addChild(leftElbow2);
        leftElbow2.setTextureOffset(0, 39).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 14.0F, scale, false);

        leftHand2 = new AdvancedModelBox(this, "leftHand2");
        leftHand2.setRotationPoint(0.0F, 0.0F, -12.0F);
        leftElbow2.addChild(leftHand2);


        leftUpperRFinger2 = new AdvancedModelBox(this, "leftUpperRFinger2");
        leftUpperRFinger2.setRotationPoint(-1.4F, -1.4F, 0.0F);
        leftHand2.addChild(leftUpperRFinger2);
        setRotationAngle(leftUpperRFinger2, 0.0F, 0.0F, -0.7854F);
        leftUpperRFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, false);

        leftLowerRFinger2 = new AdvancedModelBox(this, "leftLowerRFinger2");
        leftLowerRFinger2.setRotationPoint(-1.4F, 1.4F, 0.0F);
        leftHand2.addChild(leftLowerRFinger2);
        setRotationAngle(leftLowerRFinger2, 0.0F, 0.0F, -2.3562F);
        leftLowerRFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, false);

        leftLowerLFinger2 = new AdvancedModelBox(this, "leftLowerLFinger2");
        leftLowerLFinger2.setRotationPoint(1.4F, 1.4F, 0.0F);
        leftHand2.addChild(leftLowerLFinger2);
        setRotationAngle(leftLowerLFinger2, 0.0F, 0.0F, 2.3562F);
        leftLowerLFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, false);

        leftUpperLFinger2 = new AdvancedModelBox(this, "leftUpperLFinger2");
        leftUpperLFinger2.setRotationPoint(1.4F, -1.4F, 0.0F);
        leftHand2.addChild(leftUpperLFinger2);
        setRotationAngle(leftUpperLFinger2, 0.0F, 0.0F, 0.7854F);
        leftUpperLFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, false);

        rightArm = new AdvancedModelBox(this, "rightArm");
        rightArm.setRotationPoint(-9.0F, -16.5F, 9.0F);
        root.addChild(rightArm);
        setRotationAngle(rightArm, 0.0F, 0.0F, 0.7854F);
        rightArm.setTextureOffset(31, 23).addBox(-15.0F, -1.5F, -2.0F, 16.0F, 3.0F, 4.0F, scale, true);

        rightElbow = new AdvancedModelBox(this, "rightElbow");
        rightElbow.setRotationPoint(-15.0F, 0.0F, 0.0F);
        rightArm.addChild(rightElbow);
        rightElbow.setTextureOffset(0, 39).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 14.0F, scale, true);

        rightHand = new AdvancedModelBox(this, "rightHand");
        rightHand.setRotationPoint(0.0F, 0.0F, -12.0F);
        rightElbow.addChild(rightHand);


        rightUpperRFinger = new AdvancedModelBox(this, "rightUpperRFinger");
        rightUpperRFinger.setRotationPoint(1.4F, -1.4F, 0.0F);
        rightHand.addChild(rightUpperRFinger);
        setRotationAngle(rightUpperRFinger, 0.0F, 0.0F, 0.7854F);
        rightUpperRFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, true);

        rightLowerRFinger2 = new AdvancedModelBox(this, "rightLowerRFinger2");
        rightLowerRFinger2.setRotationPoint(1.4F, 1.4F, 0.0F);
        rightHand.addChild(rightLowerRFinger2);
        setRotationAngle(rightLowerRFinger2, 0.0F, 0.0F, 2.3562F);
        rightLowerRFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, true);

        rightLowerLFinger = new AdvancedModelBox(this, "rightLowerLFinger");
        rightLowerLFinger.setRotationPoint(-1.4F, 1.4F, 0.0F);
        rightHand.addChild(rightLowerLFinger);
        setRotationAngle(rightLowerLFinger, 0.0F, 0.0F, -2.3562F);
        rightLowerLFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, true);

        rightUpperLFinger = new AdvancedModelBox(this, "rightUpperLFinger");
        rightUpperLFinger.setRotationPoint(-1.4F, -1.4F, 0.0F);
        rightHand.addChild(rightUpperLFinger);
        setRotationAngle(rightUpperLFinger, 0.0F, 0.0F, -0.7854F);
        rightUpperLFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, true);

        rightArm2 = new AdvancedModelBox(this, "rightArm2");
        rightArm2.setRotationPoint(-6.0F, -13.5F, 9.0F);
        root.addChild(rightArm2);
        setRotationAngle(rightArm2, 0.0F, 0.0F, -0.6545F);
        rightArm2.setTextureOffset(31, 23).addBox(-15.0F, -1.5F, -2.0F, 16.0F, 3.0F, 4.0F, scale, true);

        rightElbow2 = new AdvancedModelBox(this, "rightElbow2");
        rightElbow2.setRotationPoint(-15.0F, 0.0F, 0.0F);
        rightArm2.addChild(rightElbow2);
        rightElbow2.setTextureOffset(0, 39).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 14.0F, scale, true);

        rightHand2 = new AdvancedModelBox(this, "rightHand2");
        rightHand2.setRotationPoint(0.0F, 0.0F, -12.0F);
        rightElbow2.addChild(rightHand2);


        rightUpperRFinger2 = new AdvancedModelBox(this, "rightUpperRFinger2");
        rightUpperRFinger2.setRotationPoint(1.4F, -1.4F, 0.0F);
        rightHand2.addChild(rightUpperRFinger2);
        setRotationAngle(rightUpperRFinger2, 0.0F, 0.0F, 0.7854F);
        rightUpperRFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, true);

        rightLowerRFinger3 = new AdvancedModelBox(this, "rightLowerRFinger3");
        rightLowerRFinger3.setRotationPoint(1.4F, 1.4F, 0.0F);
        rightHand2.addChild(rightLowerRFinger3);
        setRotationAngle(rightLowerRFinger3, 0.0F, 0.0F, 2.3562F);
        rightLowerRFinger3.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, true);

        rightLowerLFinger2 = new AdvancedModelBox(this, "rightLowerLFinger2");
        rightLowerLFinger2.setRotationPoint(-1.4F, 1.4F, 0.0F);
        rightHand2.addChild(rightLowerLFinger2);
        setRotationAngle(rightLowerLFinger2, 0.0F, 0.0F, -2.3562F);
        rightLowerLFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, true);

        rightUpperLFinger2 = new AdvancedModelBox(this, "rightUpperLFinger2");
        rightUpperLFinger2.setRotationPoint(-1.4F, -1.4F, 0.0F);
        rightHand2.addChild(rightUpperLFinger2);
        setRotationAngle(rightUpperLFinger2, 0.0F, 0.0F, -0.7854F);
        rightUpperLFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, scale, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }


    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        animator.update(entity);
        animator.setAnimation(EntityFarseer.ANIMATION_EMERGE);
        animator.startKeyframe(0);
        animator.move(root, 0, 0, 20);
        animator.rotate(leftArm, 0, Maths.rad(80), 0);
        animator.rotate(leftElbow, Maths.rad(-20), Maths.rad(-30), 0);
        animator.rotate(leftHand, Maths.rad(-35), Maths.rad(40), 0);
        animator.move(leftArm, -1, -5, -6);
        animator.rotate(rightArm, 0, Maths.rad(-80), 0);
        animator.rotate(rightElbow, Maths.rad(-40), Maths.rad(70), 0);
        animator.rotate(rightHand, Maths.rad(5), Maths.rad(-60), 0);
        animator.move(rightArm, 1, -5, -6);
        animator.rotate(leftArm2, 0, Maths.rad(60), 0);
        animator.rotate(leftElbow2, Maths.rad(40), Maths.rad(-30), 0);
        animator.rotate(leftHand2, Maths.rad(-15), Maths.rad(40), 0);
        animator.move(leftArm2, 2, 6, -6);
        animator.rotate(rightArm2, 0, Maths.rad(-80), 0);
        animator.rotate(rightElbow2, Maths.rad(20), Maths.rad(50), 0);
        animator.rotate(rightHand2, Maths.rad(25), Maths.rad(-60), 0);
        animator.move(rightArm2, -2, 5, -4);
        animator.endKeyframe();
        animator.setStaticKeyframe(10);
        animator.startKeyframe(10);
        animator.move(root, 0, 0, 15);
        animator.rotate(leftArm, 0, Maths.rad(70), 0);
        animator.rotate(leftElbow, Maths.rad(-20), Maths.rad(-30), 0);
        animator.rotate(leftHand, Maths.rad(-5), Maths.rad(50), 0);
        animator.move(leftArm, 1, -5, -1);
        animator.rotate(rightArm, 0, Maths.rad(-80), 0);
        animator.rotate(rightElbow, Maths.rad(-40), Maths.rad(60), 0);
        animator.rotate(rightHand, Maths.rad(5), Maths.rad(-70), 0);
        animator.move(rightArm, -1, -5, -1);
        animator.rotate(leftArm2, 0, Maths.rad(60), 0);
        animator.rotate(leftElbow2, Maths.rad(30), Maths.rad(-30), 0);
        animator.rotate(leftHand2, Maths.rad(-5), Maths.rad(40), 0);
        animator.move(leftArm2, 3, 6, -1);
        animator.rotate(rightArm2, 0, Maths.rad(-70), 0);
        animator.rotate(rightElbow2, Maths.rad(20), Maths.rad(50), 0);
        animator.rotate(rightHand2, Maths.rad(25), Maths.rad(-60), 0);
        animator.move(rightArm2, -3, 5, 1);
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.move(root, 0, 0, 10);
        animator.rotate(leftArm, 0, Maths.rad(60), 0);
        animator.rotate(leftElbow, Maths.rad(-20), Maths.rad(-30), 0);
        animator.rotate(leftHand, Maths.rad(-15), Maths.rad(50), 0);
        animator.move(leftArm, 2, -5, 4);
        animator.rotate(rightArm, 0, Maths.rad(-72), 0);
        animator.rotate(rightElbow, Maths.rad(-40), Maths.rad(60), 0);
        animator.rotate(rightHand, Maths.rad(-5), Maths.rad(-75), 0);
        animator.move(rightArm, -1, -3, 4);
        animator.rotate(leftArm2, 0, Maths.rad(55), 0);
        animator.rotate(leftElbow2, Maths.rad(30), Maths.rad(-30), 0);
        animator.rotate(leftHand2, Maths.rad(-15), Maths.rad(50), 0);
        animator.move(leftArm2, 5, 6, 4);
        animator.rotate(rightArm2, 0, Maths.rad(-60), 0);
        animator.rotate(rightElbow2, Maths.rad(20), Maths.rad(50), 0);
        animator.rotate(rightHand2, Maths.rad(5), Maths.rad(-60), 0);
        animator.move(rightArm2, -5, 5, 6);
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.move(root, 0, 0, 5);
        animator.rotate(leftArm, 0, Maths.rad(60), 0);
        animator.rotate(leftElbow, Maths.rad(-20), Maths.rad(-40), 0);
        animator.rotate(leftHand, Maths.rad(-15), Maths.rad(85), 0);
        animator.move(leftArm, 3, -5, 9);
        animator.rotate(rightArm, 0, Maths.rad(-72), 0);
        animator.rotate(rightElbow, Maths.rad(-40), Maths.rad(60), 0);
        animator.rotate(rightHand, Maths.rad(15), Maths.rad(-85), 0);
        animator.move(rightArm, -5, -4, 9);
        animator.rotate(leftArm2, 0, Maths.rad(45), 0);
        animator.rotate(leftElbow2, Maths.rad(30), Maths.rad(-40), 0);
        animator.rotate(leftHand2, Maths.rad(-5), Maths.rad(70), 0);
        animator.move(leftArm2, 8, 4, 7);
        animator.rotate(rightArm2, 0, Maths.rad(-50), 0);
        animator.rotate(rightElbow2, Maths.rad(20), Maths.rad(50), 0);
        animator.rotate(rightHand2, Maths.rad(25), Maths.rad(-70), 0);
        animator.move(rightArm2, -5, 5, 11);
        animator.endKeyframe();
        animator.resetKeyframe(10);
    }



    @Override
    public void setupAnim(EntityFarseer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, leftArm, leftElbow, leftHand, leftUpperRFinger, leftLowerRFinger, leftLowerLFinger, leftUpperLFinger, leftArm2, leftElbow2, leftHand2, leftUpperRFinger2, leftLowerRFinger2, leftLowerLFinger2, leftUpperLFinger2, rightArm, rightElbow, rightHand, rightUpperRFinger, rightLowerRFinger2, rightLowerLFinger, rightUpperLFinger, rightArm2, rightElbow2, rightHand2, rightUpperRFinger2, rightLowerRFinger3, rightLowerLFinger2, rightUpperLFinger2);
    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}