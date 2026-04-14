package com.khazoda.breakerplacer.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
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

public abstract class BaseBlock extends BlockWithEntity {
  public static final DirectionProperty FACING = Properties.FACING;
  public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
  public static final Settings defaultSettings = Settings.create().sounds(BlockSoundGroup.STONE).strength(3.5f).pistonBehavior(PistonBehavior.NORMAL).instrument(NoteBlockInstrument.BASS).mapColor(MapColor.STONE_GRAY);

  protected BaseBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.FALSE));
  }

  protected BaseBlock() {
    this(defaultSettings);
  }

  @Override
  protected BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  protected abstract void activate(ServerWorld world, BlockState state, BlockPos pos);

  protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
    BlockEntity blockEntity = world.getBlockEntity(pos);
    return blockEntity instanceof NamedScreenHandlerFactory factory ? factory : null;
  }

  /* Drop contents on destroyed */
  @Override
  public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    if (state.isOf(world.getBlockState(pos).getBlock())) return;
    BlockEntity be = world.getBlockEntity(pos);

    if (be instanceof Inventory) {
      ItemScatterer.spawn(world, pos, (Inventory) be);
      world.updateComparators(pos, this);
    }

      super.onStateReplaced(state, world, pos, newState, moved);
  }

  @Override
  protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    boolean powered =
            world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
    boolean triggered = state.get(TRIGGERED);

    if (powered && !triggered) {
      world.scheduleBlockTick(pos, this, 4);
      world.setBlockState(pos, state.with(TRIGGERED, true), Block.NOTIFY_ALL);
    } else if (!powered && triggered) {
      world.setBlockState(pos, state.with(TRIGGERED, false), Block.NOTIFY_ALL);
    }
  }

  @Override
  protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    if (!oldState.isOf(state.getBlock())) {
      if (world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up())) {
        world.scheduleBlockTick(pos, this, 4);
        world.setBlockState(pos, state.with(TRIGGERED, true), Block.NOTIFY_ALL);
      }
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
    return this.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite()).with(TRIGGERED, false);
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
