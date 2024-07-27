package com.khazoda.breakerplacer.block.entity;

import com.khazoda.breakerplacer.registry.RBlockEntity;
import com.khazoda.breakerplacer.screen.BreakerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class BreakerBlockEntity extends TemplateBlockEntity {
  public BreakerBlockEntity( BlockPos blockPos, BlockState blockState) {
    super(RBlockEntity.BREAKER_BLOCK_ENTITY, blockPos, blockState);
  }

  @Override
  protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
    return new BreakerScreenHandler(syncId, playerInventory, this);
  }
}
