package com.khazoda.breakerplacer.block.entity;

import com.khazoda.breakerplacer.registry.RBlockEntity;
import com.khazoda.breakerplacer.screen.BreakerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class BreakerBlockEntity extends TemplateBlockEntity {
  public BreakerBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(RBlockEntity.BREAKER_BLOCK_ENTITY, blockPos, blockState);
  }

  @Override
  protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
    return new BreakerScreenHandler(syncId, playerInventory, this);
  }

  /* Returns true if item is successfully added, false if not */
  public boolean addToFirstFreeSlot(ItemStack stack) {
    boolean allItemsAdded = false;
    int i = this.getMaxCount(stack);

    for (int j = 0; j < this.inventory.size(); j++) {
      ItemStack itemStack = this.inventory.get(j);
      if (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
        int k = Math.min(stack.getCount(), i - itemStack.getCount());
        if (k > 0) {
          if (itemStack.isEmpty()) {
            this.setStack(j, stack.split(k));
            allItemsAdded = true;
          } else {
            stack.decrement(k);
            itemStack.increment(k);
            allItemsAdded = true;
          }
        }
        if (stack.isEmpty()) {
          break;
        }
      }
    }
    return allItemsAdded;
  }

  @Override
  public int size() {
    return 10;
  }
}
