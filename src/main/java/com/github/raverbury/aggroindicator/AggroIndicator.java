// Source code is decompiled from a .class file using FernFlower decompiler.
package com.github.raverbury.aggroindicator;

import com.github.raverbury.aggroindicator.config.ClientConfig;
import com.github.raverbury.aggroindicator.config.ServerConfig;
import com.github.raverbury.aggroindicator.event.ClientEventHandler;
import com.github.raverbury.aggroindicator.event.ServerEventHandler;
import com.github.raverbury.aggroindicator.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("aggroindicator")
public class AggroIndicator {
   public static final String MODID = "aggroindicator";
   public static final Logger LOGGER = LogUtils.getLogger();

   public AggroIndicator() {
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      modEventBus.addListener(this::commonSetup);
      MinecraftForge.EVENT_BUS.register(this);
      ModLoadingContext.get().registerConfig(Type.SERVER, ServerConfig.INSTANCE);
      ModLoadingContext.get().registerConfig(Type.CLIENT, ClientConfig.INSTANCE);
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
         return ClientEventHandler::register;
      });
      ServerEventHandler.register();
      NetworkHandler.register();
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }
}
