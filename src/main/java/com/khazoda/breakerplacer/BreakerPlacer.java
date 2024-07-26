package com.khazoda.breakerplacer;

import com.khazoda.breakerplacer.registry.RBlock;
import net.fabricmc.api.ModInitializer;

public class BreakerPlacer implements ModInitializer {
	public static int loadedRegistries = 0;

	@Override
	public void onInitialize() {
		Constants.LOG.info("[BB&BP] Placing blocks..");

		RBlock.init();


		Constants.LOG.info("[BB&BP] {}/5 Blocks placed!",loadedRegistries);
	}
}