package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.BreakerPlacerClient;
import com.khazoda.breakerplacer.screen.BreakerScreen;
import com.khazoda.breakerplacer.screen.PlacerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class RegClientScreens {
  public static void init() {
    HandledScreens.register(RegScreenHandlers.PLACER_SCREEN_HANDLER, PlacerScreen::new);
    HandledScreens.register(RegScreenHandlers.BREAKER_SCREEN_HANDLER, BreakerScreen::new);

    BreakerPlacerClient.loadedRegistries += 1;
  }
}
