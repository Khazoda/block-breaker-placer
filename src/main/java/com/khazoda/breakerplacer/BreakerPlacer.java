package com.khazoda.breakerplacer;

import com.khazoda.breakerplacer.registry.RBlock;
import com.khazoda.breakerplacer.registry.RBlockEntity;
import com.khazoda.breakerplacer.registry.RScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class BreakerPlacer implements ModInitializer {
	public static int loadedRegistries = 0;

	@Override
	public void onInitialize() {
		Constants.LOG.info("[BB&BP] Placing blocks..");

		RBlock.init();
		RBlockEntity.init();
		RScreenHandler.init();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.addAfter(Items.CRAFTER, RBlock.PLACER_BLOCK));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.addAfter(Items.CRAFTER, RBlock.BREAKER_BLOCK));

		Constants.LOG.info("[BB&BP] {}/3 Blocks placed!",loadedRegistries);
	}
}