package com.khazoda.breakerplacer.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public abstract class BaseBlockEntity extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory<BlockPos>{
  public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);

  protected BaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
    super(blockEntityType, blockPos, blockState);
  }

  @Override
  protected DefaultedList<ItemStack> getHeldStacks() {
    return this.inventory;
  }

  @Override
  protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
    this.inventory = inventory;
  }

  public int chooseNonEmptySlot(Random random) {
    int i = -1;
    int j = 1;
    for (int k = 0; k < this.inventory.size(); k++) {
      if (!this.inventory.get(k).isEmpty() && random.nextInt(j++) == 0) {
        i = k;
      }
    }
    return i;
  }

  @Override
  protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    super.readNbt(nbt, registryLookup);
    Inventories.readNbt(nbt, inventory, registryLookup);
  }

  @Override
  protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    Inventories.writeNbt(nbt, inventory, registryLookup);
    super.writeNbt(nbt, registryLookup);
  }

  @Override
  public BlockEntityUpdateS2CPacket toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
    return pos;
  }

  @Override
  protected Text getContainerName() {
    return Text.translatable(getCachedState().getBlock().getTranslationKey());
  }

  @Override
  public int size() {
    return 3 * 3;
  }
}
