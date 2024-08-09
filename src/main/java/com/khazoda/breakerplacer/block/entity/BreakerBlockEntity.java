package com.khazoda.breakerplacer.block.entity;

import com.khazoda.breakerplacer.registry.RBlockEntity;
import com.khazoda.breakerplacer.screen.BreakerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class BreakerBlockEntity extends BaseBlockEntity implements SidedInventory {
  private static final int[] INVENTORY_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
  private static final int[] TOOL_SLOT = new int[]{9};

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

    /* .size() - 1 to prevent tool slot from receiving items */
    for (int j = 0; j < this.inventory.size() - 1; j++) {
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

  /* Prevents items being taken from tool slot */
  @Override
  public int[] getAvailableSlots(Direction side) {
    return INVENTORY_SLOTS;
  }

  @Override
  public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
    return this.isValid(slot, stack);
  }

  @Override
  public boolean canExtract(int slot, ItemStack stack, Direction dir) {
    return slot != 9;
  }


  @Override
  public int size() {
    return 10;
  }


}
