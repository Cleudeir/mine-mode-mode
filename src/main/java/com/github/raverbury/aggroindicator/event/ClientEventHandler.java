package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.AlertRenderer;
import com.github.raverbury.aggroindicator.config.ClientConfig;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ClientEventHandler {
    public ClientEventHandler() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::handleRenderLevelStageEvent);
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::handleRenderLivingEvent);
    }

    public static void handleRenderLivingEvent(RenderLivingEvent<? extends LivingEntity, ? extends EntityModel<?>> event) {
        if (!event.isCanceled() && event.getEntity().isAlive()) {
            if (shouldDrawAlert(event.getEntity())) {
                AlertRenderer.addEntity(event.getEntity());
            }
        }
    }

    public static void handleRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (!event.isCanceled()) {
            if (event.getStage() == Stage.AFTER_PARTICLES) {
                if (ClientConfig.RENDER_ALERT_ICON.get()) {
                    LocalPlayer player = Minecraft.getInstance().player;
                    ClientLevel level = Minecraft.getInstance().level;
                    if (player != null && level != null) {
                        List<Mob> nearbyMobs = level.getEntitiesWithinAABB(
                            Mob.class, 
                            player.getBoundingBox().inflate(ClientConfig.RENDER_RANGE.get()), 
                            TargetingConditions.forNonCombat().range(ClientConfig.RENDER_RANGE.get())
                        );
                        if (!nearbyMobs.isEmpty()) {
                            for (Mob mob : nearbyMobs) {
                                if (shouldDrawAlert(mob)) {
                                    AlertRenderer.addEntity(mob);
                                }
                            }
                            AlertRenderer.renderAlertIcon(event.getPartialTick(), event.getPoseStack(), Minecraft.getInstance().gameRenderer.getMainCamera());
                        }
                    }
                }
            }
        }
    }

    public static boolean shouldDrawAlert(LivingEntity clientEntity) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Entity cameraEntity = minecraftClient.getCameraEntity();
        boolean TOO_FAR_AWAY = cameraEntity == null || clientEntity.distanceTo(cameraEntity) > ClientConfig.RENDER_RANGE.get();
        if (TOO_FAR_AWAY) {
            return false;
        } else {
            boolean NOT_A_MOB = !(clientEntity instanceof Mob);
            if (NOT_A_MOB) {
                return false;
            } else {
                String entityRegistryName = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(clientEntity.getType())).toString();
                boolean IS_BLACKLISTED = false;
                for (String item : ClientConfig.CLIENT_MOB_BLACKLIST.get()) {
                    item = item.replace("*", ".*");
                    Pattern pattern = Pattern.compile(item, Pattern.CASE_INSENSITIVE);
                    if (pattern.matcher(entityRegistryName).matches()) {
                        IS_BLACKLISTED = true;
                        break;
                    }
                }
                if (IS_BLACKLISTED) {
                    return false;
                } else {
                    Player player = Minecraft.getInstance().player;
                    boolean ENTITY_IS_INVISIBLE = player == null || clientEntity.isInvisibleTo(player);
                    if (ENTITY_IS_INVISIBLE) {
                        return false;
                    } else {
                        boolean IS_TARGETING_CLIENT_PLAYER = AlertRenderer.shouldDrawThisUuid(clientEntity.getUUID());
                        if (!IS_TARGETING_CLIENT_PLAYER) {
                            return false;
                        } else {
                            boolean PLAYER_HAS_STATUS_BLINDNESS_OR_DARKNESS = player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS);
                            return !PLAYER_HAS_STATUS_BLINDNESS_OR_DARKNESS;
                        }
                    }
                }
            }
        }
    }
}
