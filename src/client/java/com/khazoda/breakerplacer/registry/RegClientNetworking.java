package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.BreakerPlacerClient;
import com.khazoda.breakerplacer.networking.BlockBreakParticlePayload;
import com.khazoda.breakerplacer.networking.ModNetworking;
import com.khazoda.breakerplacer.networking.ParticlePayload;
import com.khazoda.breakerplacer.networking.SoundPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class RegClientNetworking {
  public static void init() {
    PayloadTypeRegistry.playC2S().register(ParticlePayload.ID, ParticlePayload.CODEC);
    PayloadTypeRegistry.playC2S().register(BlockBreakParticlePayload.ID, BlockBreakParticlePayload.CODEC);
    PayloadTypeRegistry.playC2S().register(SoundPayload.ID, SoundPayload.CODEC);

    /* Particle Networking Packet Client Receipt */
    ClientPlayNetworking.registerGlobalReceiver(ParticlePayload.ID, (payload, context) -> {
      if (context.client() == null) return;
      assert context.client().player != null;
      context.client().execute(() -> {
        if (context.client().world == null)
          return;
        ModNetworking.spawnParticlesOnClient(payload.particle(), context.client().world, payload.pos(), payload.offset(), payload.particleCount(), payload.spread(), payload.iterations());
      });
    });
    /* Block Breaking Particle Networking Packet Client Receipt */
    ClientPlayNetworking.registerGlobalReceiver(BlockBreakParticlePayload.ID, (payload, context) -> {
      if (context.client() == null) return;
      assert context.client().player != null;
      context.client().execute(() -> {
        if (context.client().world == null)
          return;
        context.client().particleManager.addBlockBreakParticles(payload.pos(), payload.state());
      });
    });
    /* Sound Event Networking Packet Client Receipt */
    ClientPlayNetworking.registerGlobalReceiver(SoundPayload.ID, (payload, context) -> {
      if (context.client() == null) return;
      context.client().execute(() -> {
        if (context.client().world == null)
          return;
        ModNetworking.playSoundOnClient(payload.soundEvent(), context.client().world, payload.pos(), 1f, payload.pitch());
      });
    });

    BreakerPlacerClient.loadedRegistries += 1;
  }
}
