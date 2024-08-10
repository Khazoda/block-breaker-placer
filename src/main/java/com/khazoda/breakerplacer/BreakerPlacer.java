package com.khazoda.breakerplacer;

import com.khazoda.breakerplacer.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class BreakerPlacer implements ModInitializer {
  public static int loadedRegistries = 0;

  @Override
  public void onInitialize() {
    RegBlocks.init();
    RegBlockEntities.init();
    RegSounds.init();
    RegScreenHandlers.init();
    RegNetworking.init();

    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.addAfter(Items.CRAFTER, RegBlocks.PLACER_BLOCK));
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.addAfter(Items.CRAFTER, RegBlocks.BREAKER_BLOCK));

    Constants.LOG.info("[BB&BP] {}/5 common registries initialized!", loadedRegistries);
  }
}