package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.BreakerPlacer;
import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.block.entity.BreakerBlockEntity;
import com.khazoda.breakerplacer.block.entity.PlacerBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RBlockEntity {


  public static final BlockEntityType<PlacerBlockEntity> PLACER_BLOCK_ENTITY = Registry.register(
      Registries.BLOCK_ENTITY_TYPE, Identifier.of(Constants.NAMESPACE, "placer_block_entity"),
      BlockEntityType.Builder.create(PlacerBlockEntity::new,
          RBlock.PLACER_BLOCK).build());
  public static final BlockEntityType<BreakerBlockEntity> BREAKER_BLOCK_ENTITY = Registry.register(
      Registries.BLOCK_ENTITY_TYPE, Identifier.of(Constants.NAMESPACE, "breaker_block_entity"),
      BlockEntityType.Builder.create(BreakerBlockEntity::new,
          RBlock.BREAKER_BLOCK).build());


  public static void init() {
    BreakerPlacer.loadedRegistries += 1;
  }
}
