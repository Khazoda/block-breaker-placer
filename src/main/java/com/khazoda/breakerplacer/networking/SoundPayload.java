package com.khazoda.breakerplacer.networking;

import com.khazoda.breakerplacer.Constants;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SoundPayload(BlockPos pos, SoundEvent soundEvent,
                           float pitch) implements CustomPayload {
  public static final Id<SoundPayload> ID = new Id<>(Identifier.of(Constants.NAMESPACE, "plushable_sound_packet_without_player"));
  public static final PacketCodec<RegistryByteBuf, SoundPayload> CODEC = PacketCodec.tuple(
      BlockPos.PACKET_CODEC, SoundPayload::pos,
      SoundEvent.PACKET_CODEC, SoundPayload::soundEvent,
      PacketCodecs.FLOAT, SoundPayload::pitch,
      SoundPayload::new);

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }

  public static void sendNoPlayerPacketToClients(ServerWorld world, SoundPayload payload) {
    BlockPos builderPos = new BlockPos(payload.pos.getX(), payload.pos.getY(), payload.pos.getZ());
    /* Iterate through players that can see sound event emitter */
    PlayerLookup.tracking(world, builderPos).forEach(player -> ServerPlayNetworking.send(player, new SoundPayload(payload.pos, payload.soundEvent, payload.pitch)));
  }

}
