// Source code is decompiled from a .class file using FernFlower decompiler.
package com.github.raverbury.aggroindicator;

import com.github.raverbury.aggroindicator.config.ClientConfig;
import com.github.raverbury.aggroindicator.util.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;


public class AlertRenderer {
   private static final List<LivingEntity> renderedEntities = new ArrayList();
   private static final Set<UUID> entityUuidSet = new HashSet();
   private static final ResourceLocation ALERT_ICON = new ResourceLocation("aggroindicator:textures/alert_icon.png");

   public AlertRenderer() {
   }

   public static void addEntity(LivingEntity entity) {
      if (entity != null) {
         renderedEntities.add(entity);
      }
   }

    public static void setTarget(UUID mobUuid, UUID targetUuid) {
    Minecraft client = Minecraft.getInstance();
    Player player = client.player;
    if (player != null) {
        UUID playerUuid = player.getUUID();
        if (targetUuid != null && playerUuid.equals(targetUuid)) {
            entityUuidSet.add(mobUuid);
        } else {
            entityUuidSet.remove(mobUuid);
        }
    }
}

   public static boolean shouldDrawThisUuid(UUID uuid) {
      return entityUuidSet.contains(uuid);
   }

   public static void renderAlertIcon(float partialTick, PoseStack matrix, Camera camera) {
    Minecraft client = Minecraft.getInstance();

    // Retrieve the camera object if it's null
    if (camera == null) {
        camera = client.gameRenderer.getMainCamera();
    }

    // Clear rendered entities if the camera is still null
    if (camera == null) {
        renderedEntities.clear();
        return;
    }

    // Only proceed if there are entities to render
    if (!renderedEntities.isEmpty()) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader); // Assuming getPositionColorShader() for m_172811_
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);

        for (LivingEntity entity : renderedEntities) {
            float scaleToGui = 0.025F;
            boolean sneaking = entity.isCrouching();
            float height = entity.getBbHeight() + 0.6F - (sneaking ? 0.25F : 0.0F);
            
            double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double y = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());

            Vec3 camPos = camera.getPosition(); // Assuming getPosition() for m_90583_()
            double camX = camPos.x();
            double camY = camPos.y();
            double camZ = camPos.z();

            matrix.pushPose(); // Assuming pushPose() for m_85836_()
            matrix.translate(x - camX, y + height - camY, z - camZ); // Assuming translate() for m_85837_()
            Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
            matrix.mulPose(MathHelper.rotationDegrees(YP, -camera.getXRot())); // Assuming getYaw() for m_90590_()
            matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);

            if ((Boolean) ClientConfig.SCALE_WITH_MOB_SIZE.get()) {
                float size = (float) entity.getBoundingBox().getSize(); // Assuming getBoundingBox() for m_20191_() and getSize() for m_82309_()
                size *= size > 2.0F ? 0.9F : 1.0F;
                matrix.scale(size, size, size);
            }

            _render(matrix, (Double) ClientConfig.X_OFFSET.get(), -(7.0 + (Double) ClientConfig.Y_OFFSET.get()), ((Double) ClientConfig.ALERT_ICON_SIZE.get()).floatValue());
            matrix.popPose(); // Assuming popPose() for m_85849_()
        }

        renderedEntities.clear();
    }
}

private static void _render(PoseStack matrix, double x, double y, float size) {
    // Set the shader to be used for rendering
    RenderSystem.setShader(GameRenderer::getPositionTexShader); // Assuming getPositionTexShader() for m_172817_

    // Set the texture to the alert icon
    RenderSystem.setShaderTexture(0, ALERT_ICON);

    // Enable blending for transparency
    RenderSystem.enableBlend();

    // Get the current transformation matrix
    Matrix4f m4f = matrix.last().pose(); // Assuming last() for m_85850_() and pose() for m_252922_()

    float halfWidth = size / 2.0F;

    // Initialize the tesselator and buffer builder
    Tesselator tesselator = Tesselator.getInstance(); // Assuming getInstance() for m_85913_()
    BufferBuilder buffer = tesselator.getBuilder(); // Assuming getBuilder() for m_85915_()

    // Begin drawing quads with the specified vertex format
    buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX); // Assuming begin() for m_166779_() and POSITION_TEX for f_85817_

    // Define the vertices for the quad with the specified texture coordinates
    buffer.vertex(m4f, (float)((double)(-halfWidth) + x), (float)y, 0.25F).uv(0.0F, 0.0F).endVertex(); // Assuming vertex() for m_252986_(), uv() for m_7421_(), and endVertex() for m_5752_()
    buffer.vertex(m4f, (float)((double)(-halfWidth) + x), (float)((double)size + y), 0.25F).uv(0.0F, 1.0F).endVertex();
    buffer.vertex(m4f, (float)((double)halfWidth + x), (float)((double)size + y), 0.25F).uv(1.0F, 1.0F).endVertex();
    buffer.vertex(m4f, (float)((double)halfWidth + x), (float)y, 0.25F).uv(1.0F, 0.0F).endVertex();

    // Draw the quad
    tesselator.end(); // Assuming end() for m_85914_()
}

}
