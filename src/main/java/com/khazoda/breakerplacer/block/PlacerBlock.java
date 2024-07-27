package com.khazoda.breakerplacer.block;

import com.khazoda.breakerplacer.block.entity.PlacerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PlacerBlock extends FacingBlock implements BlockEntityProvider {
  public static final MapCodec<PlacerBlock> CODEC = createCodec(PlacerBlock::new);
  public static final DirectionProperty FACING = Properties.FACING;
  public static final Settings defaultSettings = Settings.create();

  public PlacerBlock(Settings settings) {
    super(settings);
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
      }
    }
    return ActionResult.SUCCESS;
  }

  protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
    BlockEntity blockEntity = world.getBlockEntity(pos);
    return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
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
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite());
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
    return new PlacerBlockEntity(pos,state);
  }
}