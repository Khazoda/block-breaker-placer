package com.khazoda.breakerplacer.block.entity;

import com.khazoda.breakerplacer.registry.RBlockEntity;
import com.khazoda.breakerplacer.screen.PlacerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class PlacerBlockEntity extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {
//  public final SimpleInventory inventory = new SimpleInventory(9);
//  public final InventoryStorage storage = InventoryStorage.of(inventory, null);

  public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);

  public PlacerBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(RBlockEntity.PLACER_BLOCK_ENTITY, blockPos, blockState);
  }

  @Override
  protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
    return new PlacerScreenHandler(syncId, playerInventory, this);
  }

  @Override
  protected Text getContainerName() {
    return Text.translatable(getCachedState().getBlock().getTranslationKey());
  }

  @Override
  protected DefaultedList<ItemStack> getHeldStacks() {
    return this.inventory;
  }

  @Override
  protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
    this.inventory = inventory;
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
  public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
    return pos;
  }

  @Override
  public int size() {
    return 3 * 3;
  }
}
