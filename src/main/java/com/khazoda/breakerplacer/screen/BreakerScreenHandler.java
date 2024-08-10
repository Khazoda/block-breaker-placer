package com.khazoda.breakerplacer.screen;

import com.khazoda.breakerplacer.registry.RegScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class BreakerScreenHandler extends Generic3x3ContainerScreenHandler {
  private final Inventory inventory;
  private final BlockPos pos;

  public BreakerScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
    this(syncId, playerInventory, new SimpleInventory(10));
  }

  public BreakerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
    super(syncId, playerInventory, inventory);
    this.pos = BlockPos.ORIGIN;
    checkSize(inventory, 10);
    this.inventory = inventory;
    inventory.onOpen(playerInventory.player);

    int m;
    int l;

    // Block Inventory
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 3; ++l) {
        this.addSlot(new Slot(inventory, l + m * 3, 62 + l * 18, 17 + m * 18));
      }
    }
    // Tool Slot (Global Slot ID 54)
    this.addSlot(new Slot(inventory, 9, 26, 35));


    // Player Inventory
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 9; ++l) {
        this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
      }
    }
    // Player Hotbar
    for (m = 0; m < 9; ++m) {
      this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
    }
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int slot) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot shiftClickedSlot = this.slots.get(slot);
    if (shiftClickedSlot.hasStack()) {
      ItemStack itemStackToMove = shiftClickedSlot.getStack();
      itemStack = itemStackToMove.copy();

      if (slot < 9 || slot == 54) {
        if (!this.insertItem(itemStackToMove, 9, 45, true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.insertItem(itemStackToMove, 0, 9, false)) {
        return ItemStack.EMPTY;
      }

      if (itemStackToMove.isEmpty()) {
        shiftClickedSlot.setStack(ItemStack.EMPTY);
      } else {
        shiftClickedSlot.markDirty();
      }

      if (itemStackToMove.getCount() == itemStack.getCount()) {
        return ItemStack.EMPTY;
      }

      shiftClickedSlot.onTakeItem(player, itemStackToMove);
    }

    return itemStack;
  }


  public BlockPos getPos() {
    return pos;
  }

  @Override
  public ScreenHandlerType<?> getType() {
    return RegScreenHandlers.BREAKER_SCREEN_HANDLER;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }
}