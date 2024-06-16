// Source code is decompiled from a .class file using FernFlower decompiler.
package com.github.raverbury.aggroindicator.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
   private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
   public static ForgeConfigSpec INSTANCE;
   public static ForgeConfigSpec.BooleanValue RENDER_ALERT_ICON;
   public static ForgeConfigSpec.IntValue RENDER_RANGE;
   public static ForgeConfigSpec.DoubleValue X_OFFSET;
   public static ForgeConfigSpec.DoubleValue Y_OFFSET;
   public static ForgeConfigSpec.DoubleValue ALERT_ICON_SIZE;
   public static ForgeConfigSpec.BooleanValue SCALE_WITH_MOB_SIZE;
   public static ForgeConfigSpec.ConfigValue<List<? extends String>> CLIENT_MOB_BLACKLIST;

   public ClientConfig() {
   }

   static {
      CLIENT_BUILDER.push("Rendering");
      RENDER_ALERT_ICON = CLIENT_BUILDER.comment("Controls whether the client should render alert icons").translation("config.client.renderAlertIcon").define("renderAlertIcon", true);
      RENDER_RANGE = CLIENT_BUILDER.comment("Only render alert icons for mobs within this range").translation("config.client.renderRange").defineInRange("renderRange", 32, 8, 64);
      X_OFFSET = CLIENT_BUILDER.comment("Adjusts the horizontal placement of alert icons").translation("config.client.xOffset").defineInRange("xOffset", 0.0, -10.0, 10.0);
      Y_OFFSET = CLIENT_BUILDER.comment("Adjusts the vertical placement of alert icons").translation("config.client.yOffset").defineInRange("yOffset", 0.0, -10.0, 50.0);
      ALERT_ICON_SIZE = CLIENT_BUILDER.comment("Adjust the size of alert icons").translation("config.client.alertIconSize").defineInRange("alertIconSize", 30.0, 0.0, 100.0);
      SCALE_WITH_MOB_SIZE = CLIENT_BUILDER.comment("Controls whether alert icons should grow in size with mobs").translation("config.client.scaleWithMobSize").define("scaleWithMobSize", false);
      CLIENT_MOB_BLACKLIST = CLIENT_BUILDER.comment("Do not render alert icons for these mobs").translation("config.client.clientMobBlacklist").defineList("clientMobBlacklist", new ArrayList(), (registry_name) -> {
         return true;
      });
      CLIENT_BUILDER.pop();
      INSTANCE = CLIENT_BUILDER.build();
   }
}
