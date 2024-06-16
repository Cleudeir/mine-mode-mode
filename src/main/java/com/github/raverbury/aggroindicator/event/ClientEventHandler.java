// Source code is decompiled from a .class file using FernFlower decompiler.
package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.AlertRenderer;
import com.github.raverbury.aggroindicator.config.ClientConfig;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientEventHandler {
   public ClientEventHandler() {
   }

   public static void register() {
      MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::handleRenderLevelStageEvent);
   }

   /** @deprecated */
   @Deprecated
   public static void handleRenderLivingEvent(RenderLivingEvent<? extends LivingEntity, ? extends EntityModel<?>> event) {
      if (!event.isCanceled() && event.getEntity().m_9236_().m_5776_()) {
         if (shouldDrawAlert(event.getEntity())) {
            AlertRenderer.addEntity(event.getEntity());
         }
      }
   }

   public static void handleRenderLevelStageEvent(RenderLevelStageEvent event) {
      if (!event.isCanceled()) {
         if (event.getStage() == Stage.AFTER_PARTICLES) {
            if ((Boolean)ClientConfig.RENDER_ALERT_ICON.get()) {
               LocalPlayer player = Minecraft.m_91087_().f_91074_;
               ClientLevel level = Minecraft.m_91087_().f_91073_;
               if (player != null && level != null) {
                  List<Mob> nearbyMobs = level.m_45971_(Mob.class, TargetingConditions.m_148352_().m_26883_((double)(Integer)ClientConfig.RENDER_RANGE.get()).m_26893_().m_148355_(), player, player.m_20191_().m_82400_((double)(Integer)ClientConfig.RENDER_RANGE.get()));
                  if (!nearbyMobs.isEmpty()) {
                     Iterator var4 = nearbyMobs.iterator();

                     while(var4.hasNext()) {
                        Mob mob = (Mob)var4.next();
                        if (shouldDrawAlert(mob)) {
                           AlertRenderer.addEntity(mob);
                        }
                     }

                     AlertRenderer.renderAlertIcon(event.getPartialTick(), event.getPoseStack(), Minecraft.m_91087_().f_91063_.m_109153_());
                  }
               }
            }
         }
      }
   }

   public static boolean shouldDrawAlert(LivingEntity clientEntity) {
      Minecraft minecraftClient = Minecraft.m_91087_();
      Entity cameraEntity = minecraftClient.m_91288_();
      boolean TOO_FAR_AWAY = cameraEntity == null || clientEntity.m_20270_(cameraEntity) > (float)(Integer)ClientConfig.RENDER_RANGE.get();
      if (TOO_FAR_AWAY) {
         return false;
      } else {
         boolean NOT_A_MOB = !(clientEntity instanceof Mob);
         if (NOT_A_MOB) {
            return false;
         } else {
            String entityRegistryName = ((ResourceLocation)Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(clientEntity.m_6095_()))).toString();
            boolean IS_BLACKLISTED = false;
            Iterator var7 = ((List)ClientConfig.CLIENT_MOB_BLACKLIST.get()).iterator();

            while(var7.hasNext()) {
               String item = (String)var7.next();
               item = item.replace("*", ".*");
               Pattern pattern = Pattern.compile(item, 2);
               if (pattern.matcher(entityRegistryName).matches()) {
                  IS_BLACKLISTED = true;
                  break;
               }
            }

            if (IS_BLACKLISTED) {
               return false;
            } else {
               Player player = Minecraft.m_91087_().f_91074_;
               boolean ENTITY_IS_INVISIBLE = player == null || clientEntity.m_20177_(player);
               if (ENTITY_IS_INVISIBLE) {
                  return false;
               } else {
                  boolean IS_TARGETING_CLIENT_PLAYER = AlertRenderer.shouldDrawThisUuid(clientEntity.m_20148_());
                  if (!IS_TARGETING_CLIENT_PLAYER) {
                     return false;
                  } else {
                     boolean PLAYER_HAS_STATUS_BLINDNESS_OR_DARKNESS = player.m_21023_(MobEffects.f_19610_) || player.m_21023_(MobEffects.f_216964_);
                     return !PLAYER_HAS_STATUS_BLINDNESS_OR_DARKNESS;
                  }
               }
            }
         }
      }
   }
}
