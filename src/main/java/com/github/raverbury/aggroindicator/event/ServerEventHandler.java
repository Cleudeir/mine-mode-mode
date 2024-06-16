// Source code is decompiled from a .class file using FernFlower decompiler.
package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.config.ServerConfig;
import com.github.raverbury.aggroindicator.network.NetworkHandler;
import com.github.raverbury.aggroindicator.network.packet.MobDeAggroPacket;
import com.github.raverbury.aggroindicator.network.packet.MobTargetPlayerPacket;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ServerEventHandler {
   public ServerEventHandler() {
   }

   public static void register() {
      MinecraftForge.EVENT_BUS.addListener(ServerEventHandler::handleLivingChangeTargetEvent);
      MinecraftForge.EVENT_BUS.addListener(ServerEventHandler::handleLivingDeathEvent);
   }

   public static void handleLivingChangeTargetEvent(LivingChangeTargetEvent event) {
      if (!event.isCanceled() && event.getEntity() != null && !event.getEntity().m_9236_().m_5776_()) {
         if (shouldSendDeAggroPacket(event)) {
            NetworkHandler.sendToPlayer(new MobDeAggroPacket(event.getEntity().m_20148_()), (ServerPlayer)getCurrentTarget(event.getEntity()));
         }

         if (shouldSendAggroPacket(event)) {
            NetworkHandler.sendToPlayer(new MobTargetPlayerPacket(event.getEntity().m_20148_(), event.getNewTarget().m_20148_()), (ServerPlayer)event.getNewTarget());
         }

      }
   }

   public static void handleLivingDeathEvent(LivingDeathEvent event) {
      if (!event.isCanceled() && event.getEntity() != null && !event.getEntity().m_9236_().m_5776_()) {
         if (shouldSendDeAggroPacket(event)) {
            ServerPlayer serverPlayer = (ServerPlayer)((Mob)event.getEntity()).m_5448_();
            NetworkHandler.sendToPlayer(new MobDeAggroPacket(event.getEntity().m_20148_()), serverPlayer);
         }

      }
   }

   private static boolean shouldSendDeAggroPacket(LivingDeathEvent event) {
      LivingEntity target = getCurrentTarget(event.getEntity());
      boolean WAS_TARGETING_PLAYER = target instanceof ServerPlayer;
      return WAS_TARGETING_PLAYER;
   }

   private static boolean shouldSendDeAggroPacket(LivingChangeTargetEvent event) {
      LivingEntity oldTarget = getCurrentTarget(event.getEntity());
      LivingEntity newTarget = event.getNewTarget();
      boolean WAS_TARGETING_PLAYER = oldTarget instanceof ServerPlayer;
      boolean NEW_TARGET_IS_DIFFERENT = newTarget == null || !newTarget.m_7306_(oldTarget);
      return WAS_TARGETING_PLAYER && NEW_TARGET_IS_DIFFERENT;
   }

   private static boolean shouldSendAggroPacket(LivingChangeTargetEvent event) {
      LivingEntity oldTarget = getCurrentTarget(event.getEntity());
      LivingEntity newTarget = event.getNewTarget();
      String entityRegistryName = ((ResourceLocation)Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().m_6095_()))).toString();
      boolean IS_BLACKLISTED = false;
      Iterator var5 = ((List)ServerConfig.SERVER_MOB_BLACKLIST.get()).iterator();

      while(var5.hasNext()) {
         String item = (String)var5.next();
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
         boolean IS_TARGETING_PLAYER = newTarget instanceof ServerPlayer;
         boolean NEW_TARGET_IS_DIFFERENT = oldTarget == null || !oldTarget.m_7306_(newTarget);
         return IS_TARGETING_PLAYER && NEW_TARGET_IS_DIFFERENT;
      }
   }

   private static LivingEntity getCurrentTarget(LivingEntity entity) {
      if (!(entity instanceof Mob mob)) {
         return null;
      } else {
         return mob.m_5448_();
      }
   }
}
