package com.khazoda.breakerplacer.screen;

import com.khazoda.breakerplacer.registry.RScreenHandler;
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
    this(syncId, playerInventory, new SimpleInventory(9));
  }

  public BreakerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
    super(syncId, playerInventory, inventory);
    this.pos = BlockPos.ORIGIN;
    checkSize(inventory, 9);
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

  public BlockPos getPos() {
    return pos;
  }

  @Override
  public ScreenHandlerType<?> getType() {
    return RScreenHandler.PLACER_SCREEN_HANDLER;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int invSlot) {
    ItemStack newStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(invSlot);
    if (slot != null && slot.hasStack()) {
      ItemStack originalStack = slot.getStack();
      newStack = originalStack.copy();
      if (invSlot < this.inventory.size()) {
        if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
        return ItemStack.EMPTY;
      }
      if (originalStack.isEmpty()) {
        slot.setStack(ItemStack.EMPTY);
      } else {
        slot.markDirty();
      }
    }
    return newStack;
  }
}