package com.khazoda.breakerplacer;

import com.khazoda.breakerplacer.registry.RegClientNetworking;
import com.khazoda.breakerplacer.registry.RegClientScreens;
import net.fabricmc.api.ClientModInitializer;

public class BreakerPlacerClient implements ClientModInitializer {
  public static int loadedRegistries = 0;

  @Override
  public void onInitializeClient() {

    RegClientScreens.init();
    RegClientNetworking.init();

    Constants.LOG.info("[BB&BP] {}/2 client registries initialized!", loadedRegistries);
  }
}