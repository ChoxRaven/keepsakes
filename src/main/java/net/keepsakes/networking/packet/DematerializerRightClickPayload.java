package net.keepsakes.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DematerializerRightClickPayload(BlockPos targetPos) implements CustomPayload {
    public static final CustomPayload.Id<DematerializerRightClickPayload> ID =
            new CustomPayload.Id<>(Identifier.of("keepsakes", "dematerializer_right_click"));

    public static final PacketCodec<PacketByteBuf, DematerializerRightClickPayload> CODEC =
            PacketCodec.of((value, buf) -> {
                buf.writeBlockPos(value.targetPos);
            }, buf -> {
                return new DematerializerRightClickPayload(buf.readBlockPos());
            });

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);
    }
}