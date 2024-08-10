package com.khazoda.breakerplacer.block.entity;

import com.khazoda.breakerplacer.registry.RegBlockEntities;
import com.khazoda.breakerplacer.screen.PlacerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class PlacerBlockEntity extends BaseBlockEntity {

  public PlacerBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(RegBlockEntities.PLACER_BLOCK_ENTITY, blockPos, blockState);
  }

  @Override
  protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
    return new PlacerScreenHandler(syncId, playerInventory, this);
  }
}
