package com.khazoda.breakerplacer.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;


public class ModNetworking {

    /* Call this method after sending packet when wanting to spawn particles */
    public static void spawnParticlesOnClient(ParticleEffect particleType, World world, BlockPos pos, int particleCount, Vec3d offset, float spread) {
        try {
            Vec3d vec = pos.toCenterPos();
            Random rand = new Random();
            for (int i = 0; i < particleCount; i++) {
                if (spread == 0)
                    world.addParticle(particleType, vec.x + offset.x, vec.y + offset.y, vec.z + offset.x, 0, 0, 0);
                if (spread != 0) world.addParticle(particleType, vec.x + offset.x, vec.y + offset.y, vec.z + offset.x,
                        rand.nextFloat(-spread, spread), rand.nextFloat(-spread, spread), rand.nextFloat(-spread, spread));
            }
        } catch (Exception e) {
            System.out.println("Caught log-in animation exception");
        }
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