package com.khazoda.breakerplacer.block;

import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.block.entity.BreakerBlockEntity;
import com.khazoda.breakerplacer.networking.BlockBreakParticlePayload;
import com.khazoda.breakerplacer.registry.RBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.minecraft.block.DoorBlock.HALF;

public class BreakerBlock extends BaseBlock {
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
      Constants.LOG.warn("No matching block entity at {}, skipping block break attempt", pos);
    } else {
      world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(be.getCachedState()));
      if (state.isAir()) return;

      Direction direction = state.get(Properties.FACING);

      BlockPos targetPos = pos.offset(direction);
      BlockState targetBlockState = world.getBlockState(targetPos);
      Block targetBlock = targetBlockState.getBlock();
      BlockEntity targetBE = targetBlockState.hasBlockEntity() ? world.getBlockEntity(targetPos) : null;

      /* Get breaker's held tool to break blocks with */
      ItemStack toolToBreakWith = be.inventory.get(9);
      try {
        //world.getPlayers().getFirst().sendMessage(Text.literal(getDroppedStacks(targetBlockState, world, targetPos, targetBE, toolForBreaking).toString() + "  |  " + toolForBreaking));

        List<ItemStack> l = getDroppedStacks(targetBlockState, world, targetPos, targetBE, toolToBreakWith);
        l.forEach(stack -> {
              // Drop items on floor if breaker is full
              if (!be.addToFirstFreeSlot(stack)) {
                dropStacks(targetBlockState, world, targetPos, targetBE);
              }

              // Remove broken block from the world expeditiously
              world.setBlockState(targetPos, targetBlockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);

              // Do post-block break update stuff
              world.updateNeighbors(targetPos, targetBlock);
              state.updateNeighbors(world, targetPos, Block.NOTIFY_LISTENERS);
              world.emitGameEvent(GameEvent.BLOCK_DESTROY, targetPos, GameEvent.Emitter.of(targetBlockState));

              // Play sound and show block breaking particles to clients nearby
              BlockBreakParticlePayload.sendBlockBreakParticlePayloadToClients(world, new BlockBreakParticlePayload(targetPos, targetBlockState));
              world.playSound(
                  null,
                  targetPos,
                  targetBlockState.getSoundGroup().getBreakSound(),
                  SoundCategory.BLOCKS,
                  1f,
                  1f
              );

            }
        );
        // If no blocks were broken play a failure sound
        if (l.isEmpty() && targetBlock != Blocks.AIR) {
          world.playSound(
              null,
              targetPos,
              SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
              SoundCategory.BLOCKS,
              1f,
              1f
          );
        }
      } catch (Exception e) {
        Constants.LOG.warn("Failed to add block ItemStack to breaker. {}", e.getMessage());
      }
      be.markDirty();

    }
  }

  public List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, ItemStack tool) {
    CachedBlockPosition cachedPos = new CachedBlockPosition(world, pos, false);
    RegistryKey<LootTable> registryKey = state.getBlock().getLootTableKey();
    if (registryKey == LootTables.EMPTY) return Collections.emptyList();

    LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
        .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
        .add(LootContextParameters.TOOL, tool)
        .add(LootContextParameters.BLOCK_STATE, state)
        .addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity);

    LootContextParameterSet parameterSet = builder.build(LootContextTypes.BLOCK);
    ServerWorld serverWorld = parameterSet.getWorld();
    LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable(registryKey);

    if (state.isToolRequired()) {
      if (tool.canBreak(cachedPos) || tool.getItem().isCorrectForDrops(tool, state)) {
        return lootTable.generateLoot(parameterSet);
      }
    } else {
      return lootTable.generateLoot(parameterSet);
    }
    return Collections.emptyList();
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
