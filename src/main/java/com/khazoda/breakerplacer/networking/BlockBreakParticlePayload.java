package com.khazoda.breakerplacer.networking;

import com.khazoda.breakerplacer.Constants;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record BlockBreakParticlePayload(BlockPos pos, BlockState state) implements CustomPayload {
  public static final CustomPayload.Id<BlockBreakParticlePayload> ID = new CustomPayload.Id<>(Identifier.of(Constants.NAMESPACE, "block_break_particle_packet"));

  public static final PacketCodec<RegistryByteBuf, BlockBreakParticlePayload> CODEC = PacketCodec.tuple(
      BlockPos.PACKET_CODEC, BlockBreakParticlePayload::pos,
      PacketCodecs.codec(BlockState.CODEC), BlockBreakParticlePayload::state,
      BlockBreakParticlePayload::new);

  @Override
  public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }

  public static void sendBlockBreakParticlePayloadToClients(ServerWorld world, BlockBreakParticlePayload payload) {
    BlockPos builderPos = new BlockPos(payload.pos.getX(), payload.pos.getY(), payload.pos.getZ());
    /* Iterate through players that can see particle event emitter */
    PlayerLookup.tracking(world, builderPos).forEach(player -> ServerPlayNetworking.send(player, new BlockBreakParticlePayload(payload.pos, payload.state)));
  }
}
