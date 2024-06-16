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
      Minecraft client = Minecraft.m_91087_();
      Player player = client.f_91074_;
      if (player != null) {
         if (targetUuid != null && player.m_20148_().equals(targetUuid)) {
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
      Minecraft client = Minecraft.m_91087_();
      if (camera == null) {
         camera = client.m_91290_().f_114358_;
      }

      if (camera == null) {
         renderedEntities.clear();
      } else if (!renderedEntities.isEmpty()) {
         RenderSystem.setShader(GameRenderer::m_172811_);
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(770, 771, 1, 0);
         Iterator var4 = renderedEntities.iterator();

         while(var4.hasNext()) {
            LivingEntity entity = (LivingEntity)var4.next();
            float scaleToGui = 0.025F;
            boolean sneaking = entity.m_6047_();
            float height = entity.m_20206_() + 0.6F - (sneaking ? 0.25F : 0.0F);
            double x = Mth.m_14139_((double)partialTick, entity.f_19854_, entity.m_20185_());
            double y = Mth.m_14139_((double)partialTick, entity.f_19855_, entity.m_20186_());
            double z = Mth.m_14139_((double)partialTick, entity.f_19856_, entity.m_20189_());
            Vec3 camPos = camera.m_90583_();
            double camX = camPos.m_7096_();
            double camY = camPos.m_7098_();
            double camZ = camPos.m_7094_();
            matrix.m_85836_();
            matrix.m_85837_(x - camX, y + (double)height - camY, z - camZ);
            Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
            matrix.m_252781_(MathHelper.rotationDegrees(YP, -camera.m_90590_()));
            matrix.m_85841_(-scaleToGui, -scaleToGui, scaleToGui);
            if ((Boolean)ClientConfig.SCALE_WITH_MOB_SIZE.get()) {
               float size = (float)entity.m_20191_().m_82309_();
               size *= size > 2.0F ? 0.9F : 1.0F;
               matrix.m_85841_(size, size, size);
            }

            _render(matrix, (Double)ClientConfig.X_OFFSET.get(), -(7.0 + (Double)ClientConfig.Y_OFFSET.get()), ((Double)ClientConfig.ALERT_ICON_SIZE.get()).floatValue());
            matrix.m_85849_();
         }

         renderedEntities.clear();
      }
   }

   private static void _render(PoseStack matrix, double x, double y, float size) {
      RenderSystem.setShader(GameRenderer::m_172817_);
      RenderSystem.setShaderTexture(0, ALERT_ICON);
      RenderSystem.enableBlend();
      Matrix4f m4f = matrix.m_85850_().m_252922_();
      float halfWidth = size / 2.0F;
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder buffer = tesselator.m_85915_();
      buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85817_);
      buffer.m_252986_(m4f, (float)((double)(-halfWidth) + x), (float)y, 0.25F).m_7421_(0.0F, 0.0F).m_5752_();
      buffer.m_252986_(m4f, (float)((double)(-halfWidth) + x), (float)((double)size + y), 0.25F).m_7421_(0.0F, 1.0F).m_5752_();
      buffer.m_252986_(m4f, (float)((double)halfWidth + x), (float)((double)size + y), 0.25F).m_7421_(1.0F, 1.0F).m_5752_();
      buffer.m_252986_(m4f, (float)((double)halfWidth + x), (float)y, 0.25F).m_7421_(1.0F, 0.0F).m_5752_();
      tesselator.m_85914_();
   }
}
