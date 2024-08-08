package com.khazoda.breakerplacer;

import com.khazoda.breakerplacer.registry.RScreenHandler;
import com.khazoda.breakerplacer.screen.BreakerScreen;
import com.khazoda.breakerplacer.screen.PlacerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BreakerPlacerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(RScreenHandler.PLACER_SCREEN_HANDLER, PlacerScreen::new);
		HandledScreens.register(RScreenHandler.BREAKER_SCREEN_HANDLER, BreakerScreen::new);

	}
}