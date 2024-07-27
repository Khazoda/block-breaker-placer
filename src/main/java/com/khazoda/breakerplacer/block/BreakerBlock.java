package com.khazoda.breakerplacer.block;

import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.block.entity.BreakerBlockEntity;
import com.khazoda.breakerplacer.registry.RBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BreakerBlock extends TemplateBlock {
  public static final MapCodec<BreakerBlock> CODEC = createCodec(BreakerBlock::new);

  public BreakerBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.FALSE));
  }

  public BreakerBlock() {
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
    BreakerBlockEntity be = world.getBlockEntity(pos, RBlockEntity.BREAKER_BLOCK_ENTITY).orElse(null);
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
  protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    this.activate(world, state, pos);
  }

  @Override
  protected MapCodec<? extends BreakerBlock> getCodec() {
    return CODEC;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new BreakerBlockEntity(pos, state);
  }
}
