package com.khazoda.breakerplacer.block;

import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.block.entity.PlacerBlockEntity;
import com.khazoda.breakerplacer.registry.RBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class PlacerBlock extends FacingBlock implements BlockEntityProvider {
  public static final MapCodec<PlacerBlock> CODEC = createCodec(PlacerBlock::new);
  public static final DirectionProperty FACING = Properties.FACING;
  public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
  public static final Settings defaultSettings = Settings.create();

  public PlacerBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.FALSE));
  }

  public PlacerBlock() {
    this(defaultSettings);
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
    if (!world.isClient) {
      NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
      if (screenHandlerFactory != null) {
        player.openHandledScreen(screenHandlerFactory);
        return ActionResult.CONSUME;
      }
    }
    return ActionResult.SUCCESS;
  }

  protected void activate(ServerWorld world, BlockState state, BlockPos pos) {
    PlacerBlockEntity be = (PlacerBlockEntity) world.getBlockEntity(pos, RBlockEntity.PLACER_BLOCK_ENTITY).orElse(null);
    if (be == null) {
      Constants.LOG.warn("Ignoring dispensing attempt for Dispenser without matching block entity at {}", pos);
    } else {
      int i = be.chooseNonEmptySlot(world.random);
      if (i < 0) {
        world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(be.getCachedState()));
      } else {
        ItemStack itemStack = be.getStack(i);
        Direction direction = state.get(Properties.FACING);
        Direction direction2 = world.isAir(pos.down()) ? direction : Direction.UP;
        be.setStack(i, placeBlock(world, direction, pos.offset(direction), direction2, itemStack));
      }
    }
  }

  public static ItemStack placeBlock(ServerWorld world, Direction direction, BlockPos pos, Direction direction2, ItemStack itemStack) {
    Item item = itemStack.getItem();
    if (item instanceof BlockItem) {
      try {
        ((BlockItem) item).place(new AutomaticItemPlacementContext(world, pos, direction, itemStack, direction2));
      } catch (Exception var8) {
        Constants.LOG.error("Error trying to place block at {}", pos, var8);
      }
    }
    return itemStack;
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

  @Override
  protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    this.activate(world, state, pos);
  }

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

  @Override
  protected MapCodec<? extends PlacerBlock> getCodec() {
    return CODEC;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new PlacerBlockEntity(pos, state);
  }
}