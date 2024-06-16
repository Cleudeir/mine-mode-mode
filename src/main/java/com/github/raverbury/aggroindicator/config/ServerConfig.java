// Source code is decompiled from a .class file using FernFlower decompiler.
package com.github.raverbury.aggroindicator.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
   private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
   public static ForgeConfigSpec INSTANCE;
   public static ForgeConfigSpec.ConfigValue<List<? extends String>> SERVER_MOB_BLACKLIST;

   public ServerConfig() {
   }

   static {
      SERVER_BUILDER.push("Targeting");
      SERVER_MOB_BLACKLIST = SERVER_BUILDER.comment("Do not check for target acquisition for these mobs").translation("config.server.serverMobBlacklist").defineList("serverMobBlacklist", new ArrayList(), (registry_name) -> {
         return true;
      });
      SERVER_BUILDER.pop();
      INSTANCE = SERVER_BUILDER.build();
   }
}
