package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.BreakerPlacer;
import com.khazoda.breakerplacer.networking.BlockBreakParticlePayload;
import com.khazoda.breakerplacer.networking.ParticlePayload;
import com.khazoda.breakerplacer.networking.SoundPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class RNetworking {

  public static void init() {
    PayloadTypeRegistry.playS2C().register(ParticlePayload.ID, ParticlePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(BlockBreakParticlePayload.ID, BlockBreakParticlePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(SoundPayload.ID, SoundPayload.CODEC);

    BreakerPlacer.loadedRegistries += 1;
  }
}
