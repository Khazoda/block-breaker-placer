package com.khazoda.breakerplacer.networking;

import com.khazoda.breakerplacer.Constants;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record ParticlePayload(ParticleEffect particle, BlockPos pos, Vec3d offset,
                              float spread, byte iterations, byte particleCount) implements CustomPayload {
  public static final Id<ParticlePayload> ID = new Id<>(Identifier.of(Constants.NAMESPACE, "particle_packet"));

  public static final PacketCodec<RegistryByteBuf, ParticlePayload> CODEC = PacketCodec.tuple(
      ParticleTypes.PACKET_CODEC, ParticlePayload::particle,
      BlockPos.PACKET_CODEC, ParticlePayload::pos,
      PacketCodecs.codec(Vec3d.CODEC), ParticlePayload::offset,
      PacketCodecs.FLOAT, ParticlePayload::spread,
      PacketCodecs.BYTE, ParticlePayload::iterations,
      PacketCodecs.BYTE, ParticlePayload::particleCount,
      ParticlePayload::new);


  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }

  public static void sendParticlePacketToClients(ServerWorld world, ParticlePayload payload) {
    BlockPos builderPos = new BlockPos(payload.pos.getX(), payload.pos.getY(), payload.pos.getZ());
    /* Iterate through players that can see particle event emitter */
    PlayerLookup.tracking(world, builderPos).forEach(player -> {
      ServerPlayNetworking.send(player, new ParticlePayload(payload.particle, payload.pos, payload.offset, payload.spread, payload.iterations, payload.particleCount));
    });
  }
}

