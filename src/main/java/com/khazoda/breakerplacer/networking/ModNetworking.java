package com.khazoda.breakerplacer.networking;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class ModNetworking {

  /* Call this method after sending packet when wanting to spawn particles */
  public static void spawnParticlesOnClient(ParticleEffect particleType, World world, BlockPos pos, Vec3d offset, int particleCount, float velocityMagnitude, byte iterations) {
    try {
      Vec3d center = pos.toCenterPos();
      Random r = world.random;

      for (int i = 0; i < iterations; i++) {
        float x = randomFloatBetween(r.nextFloat(), -0.48f, 0.48f);
        float y = randomFloatBetween(r.nextFloat(), -0.48f, 0.48f);
        float z = randomFloatBetween(r.nextFloat(), -0.48f, 0.48f);

        for (int j = 0; j < particleCount; j++) {
          world.addParticle(particleType, center.x + x + offset.x, center.y + y + offset.y, center.z + z + offset.z,
              randomVelocity(r.nextFloat(), velocityMagnitude),
              randomVelocity(r.nextFloat(), velocityMagnitude),
              randomVelocity(r.nextFloat(), velocityMagnitude));
        }
      }

    } catch (Exception e) {
      System.out.println("Caught log-in animation exception");
    }
  }

  private static float randomVelocity(float r, float spread) {
    return ((((r * 2) - 1) / 2) * spread);
  }

  private static float randomFloatBetween(float r, float min, float max) {
    return ((((r * (max - min)) + min)));
  }

  /* Call this method clientside after sending packet when wanting to play sound */
  public static void playSoundOnClient(SoundEvent sound, World world, BlockPos pos, float volume, float pitch) {
    try {
      Vec3d vec = pos.toCenterPos();
      world.playSoundAtBlockCenter(BlockPos.ofFloored(vec), sound, SoundCategory.BLOCKS, volume, pitch, true);
    } catch (Exception e) {
      System.out.println("Caught sound exception");
    }
  }


}