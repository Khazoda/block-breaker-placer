package com.khazoda.breakerplacer.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class TemplateBlock extends FacingBlock implements BlockEntityProvider {
  public static final DirectionProperty FACING = Properties.FACING;
  public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
  public static final Settings defaultSettings = Settings.create();

  protected TemplateBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.FALSE));
  }

  protected TemplateBlock() {
    this(defaultSettings);
  }

  protected abstract void activate(ServerWorld world, BlockState state, BlockPos pos);

  protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
    BlockEntity blockEntity = world.getBlockEntity(pos);
    return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) blockEntity : null;
  }

  /* Drop contents on destroyed */
  @Override
  public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    if (state.getBlock() != newState.getBlock()) {
      BlockEntity be = world.getBlockEntity(pos);

      if (be instanceof Inventory) {
        ItemScatterer.spawn(world, pos, (Inventory) be);
        world.updateComparators(pos, this);
      }

      super.onStateReplaced(state, world, pos, newState, moved);
    }
  }

  @Override
  protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
    boolean bl2 = state.get(TRIGGERED);
    if (bl && !bl2) {
      world.scheduleBlockTick(pos, this, 4);
      world.setBlockState(pos, state.with(TRIGGERED, Boolean.TRUE), Block.NOTIFY_LISTENERS);
    } else if (!bl && bl2) {
      world.setBlockState(pos, state.with(TRIGGERED, Boolean.FALSE), Block.NOTIFY_LISTENERS);
    }
  }

 protected abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, TRIGGERED);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite());
  }

  @Override
  protected boolean hasComparatorOutput(BlockState state) {
    return true;
  }

  @Override
  protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
    return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
  }

  @Override
  protected BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Override
  protected BlockState mirror(BlockState state, BlockMirror mirror) {
    return state.rotate(mirror.getRotation(state.get(FACING)));
  }

}
