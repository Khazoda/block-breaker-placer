package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.BreakerPlacer;
import com.khazoda.breakerplacer.Constants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class RegSounds {
  public static final SoundEvent BREAK = register("break");
  public static final SoundEvent FAIL = register("fail");

  public static void init() {
    BreakerPlacer.loadedRegistries += 1;
  }

  private static SoundEvent register(String name) {
    return Registry.register(Registries.SOUND_EVENT, name, SoundEvent.of(Identifier.of(Constants.NAMESPACE, name)));
  }
}
