// Source code is decompiled from a .class file using FernFlower decompiler.
package com.github.raverbury.aggroindicator.network.packet;

import com.github.raverbury.aggroindicator.AlertRenderer;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class MobTargetPlayerPacket {
   public final UUID mobUuid;
   public final UUID playerUuid;

   public MobTargetPlayerPacket(UUID _mobUuid, UUID _playerUuid) {
      this.mobUuid = _mobUuid;
      this.playerUuid = _playerUuid;
   }

   public MobTargetPlayerPacket(FriendlyByteBuf buf) {
      this.mobUuid = buf.m_130259_();
      this.playerUuid = buf.m_130259_();
   }

   public void encode(FriendlyByteBuf buf) {
      buf.m_130077_(this.mobUuid);
      buf.m_130077_(this.playerUuid);
   }

   public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
      NetworkEvent.Context context = (NetworkEvent.Context)contextSupplier.get();
      context.enqueueWork(() -> {
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
            return () -> {
               AlertRenderer.setTarget(this.mobUuid, this.playerUuid);
            };
         });
      });
      context.setPacketHandled(true);
      return true;
   }
}
