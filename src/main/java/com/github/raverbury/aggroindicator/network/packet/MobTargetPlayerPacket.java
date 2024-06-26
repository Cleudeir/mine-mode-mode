package com.github.raverbury.aggroindicator.network.packet;

import com.github.raverbury.aggroindicator.AlertRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MobTargetPlayerPacket {
    public final UUID mobUuid;
    public final UUID playerUuid;

    public MobTargetPlayerPacket(UUID mobUuid, UUID playerUuid) {
        this.mobUuid = mobUuid;
        this.playerUuid = playerUuid;
    }

    public MobTargetPlayerPacket(FriendlyByteBuf buf) {
        this.mobUuid = buf.readUUID();
        this.playerUuid = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.mobUuid);
        buf.writeUUID(this.playerUuid);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                AlertRenderer.setTarget(this.mobUuid, this.playerUuid);
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
