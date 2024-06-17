package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.config.ServerConfig;
import com.github.raverbury.aggroindicator.network.NetworkHandler;
import com.github.raverbury.aggroindicator.network.packet.MobDeAggroPacket;
import com.github.raverbury.aggroindicator.network.packet.MobTargetPlayerPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ServerEventHandler {
    public ServerEventHandler() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandler::handleLivingChangeTargetEvent);
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandler::handleLivingDeathEvent);
    }

    public static void handleLivingChangeTargetEvent(LivingChangeTargetEvent event) {
        if (!event.isCanceled() && event.getEntity() != null && event.getEntity().isAlive()) {
            if (shouldSendDeAggroPacket(event)) {
                LivingEntity oldTarget = getCurrentTarget(event.getEntity());
                if (oldTarget instanceof ServerPlayer serverPlayer) {
                    NetworkHandler.sendToPlayer(new MobDeAggroPacket(event.getEntity().getUUID()), serverPlayer);
                }
            }

            if (shouldSendAggroPacket(event)) {
                LivingEntity newTarget = event.getNewTarget();
                if (newTarget instanceof ServerPlayer serverPlayer) {
                    NetworkHandler.sendToPlayer(new MobTargetPlayerPacket(event.getEntity().getUUID(), newTarget.getUUID()), serverPlayer);
                }
            }
        }
    }

    public static void handleLivingDeathEvent(LivingDeathEvent event) {
        if (!event.isCanceled() && event.getEntity() != null && event.getEntity().isAlive()) {
            if (shouldSendDeAggroPacket(event)) {
                LivingEntity oldTarget = getCurrentTarget(event.getEntity());
                if (oldTarget instanceof ServerPlayer serverPlayer) {
                    NetworkHandler.sendToPlayer(new MobDeAggroPacket(event.getEntity().getUUID()), serverPlayer);
                }
            }
        }
    }

    private static boolean shouldSendDeAggroPacket(LivingDeathEvent event) {
        LivingEntity target = getCurrentTarget(event.getEntity());
        return target instanceof ServerPlayer;
    }

    private static boolean shouldSendDeAggroPacket(LivingChangeTargetEvent event) {
        LivingEntity oldTarget = getCurrentTarget(event.getEntity());
        LivingEntity newTarget = event.getNewTarget();
        boolean wasTargetingPlayer = oldTarget instanceof ServerPlayer;
        boolean newTargetIsDifferent = newTarget == null || !newTarget.equals(oldTarget);
        return wasTargetingPlayer && newTargetIsDifferent;
    }

    private static boolean shouldSendAggroPacket(LivingChangeTargetEvent event) {
        LivingEntity oldTarget = getCurrentTarget(event.getEntity());
        LivingEntity newTarget = event.getNewTarget();
        String entityRegistryName = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType())).toString();
        boolean isBlacklisted = isEntityBlacklisted(entityRegistryName);

        if (isBlacklisted) {
            return false;
        } else {
            boolean isTargetingPlayer = newTarget instanceof ServerPlayer;
            boolean newTargetIsDifferent = oldTarget == null || !oldTarget.equals(newTarget);
            return isTargetingPlayer && newTargetIsDifferent;
        }
    }

    private static boolean isEntityBlacklisted(String entityRegistryName) {
        for (String item : ServerConfig.SERVER_MOB_BLACKLIST.get()) {
            item = item.replace("*", ".*");
            Pattern pattern = Pattern.compile(item, Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(entityRegistryName).matches()) {
                return true;
            }
        }
        return false;
    }

    private static LivingEntity getCurrentTarget(LivingEntity entity) {
        if (entity instanceof Mob mob) {
            return mob.getTarget();
        }
        return null;
    }
}
