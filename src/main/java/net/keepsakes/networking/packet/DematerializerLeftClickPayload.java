package net.keepsakes.networking.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DematerializerLeftClickPayload() implements CustomPayload {
    public static final CustomPayload.Id<DematerializerLeftClickPayload> ID =
            new CustomPayload.Id<>(Identifier.of("keepsakes", "dematerializer_left_click"));

    public static final PacketCodec<RegistryByteBuf, DematerializerLeftClickPayload> CODEC =
            PacketCodec.unit(new DematerializerLeftClickPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}