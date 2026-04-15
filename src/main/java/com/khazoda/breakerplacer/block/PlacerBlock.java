package com.khazoda.breakerplacer.block;

import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.block.entity.PlacerBlockEntity;
import com.khazoda.breakerplacer.networking.ParticlePayload;
import com.khazoda.breakerplacer.registry.RegBlockEntities;
import com.khazoda.breakerplacer.registry.RegSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
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
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class PlacerBlock extends BaseBlock {
  public static final MapCodec<PlacerBlock> CODEC = createCodec(PlacerBlock::new);

  public PlacerBlock(Settings settings) {
    super(settings.sounds(BlockSoundGroup.STONE).strength(3.5f).pistonBehavior(PistonBehavior.NORMAL).instrument(NoteBlockInstrument.BASS).mapColor(MapColor.STONE_GRAY));
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
    PlacerBlockEntity be = world.getBlockEntity(pos, RegBlockEntities.PLACER_BLOCK_ENTITY).orElse(null);
    if (be == null) {
      Constants.LOG.warn("No matching block entity at {}, skipping block placement", pos);
    } else {
      int i = be.chooseNonEmptySlot(world.random);
      world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(be.getCachedState()));
      if (i < 0) {
        world.playSound(
            null,
            pos,
            SoundEvents.BLOCK_DISPENSER_FAIL,
            SoundCategory.BLOCKS,
            1f,
            1.2f
        );
      } else {
        ItemStack itemStack = be.getStack(i);
        Direction direction = state.get(Properties.FACING);
        be.setStack(i, placeBlock(world, direction, pos.offset(direction), itemStack));
        be.markDirty();
      }
    }
  }

  protected static ItemStack placeBlock(ServerWorld world, Direction direction, BlockPos pos, ItemStack itemStack) {
    Item item = itemStack.getItem();
    if (item instanceof BlockItem) {
      try {
        /* Places block, and if placement fails (i.e. a block is already in the placement spot), play the error sound */
        AutomaticItemPlacementContext context = new AutomaticItemPlacementContext(world, pos, direction, itemStack, direction) {
          @Override
          public Direction getPlayerLookDirection() {
            return this.getSide().getOpposite();
          }
          @Override
          public Direction getVerticalPlayerLookDirection() {
            return this.getSide().getAxis() == Direction.Axis.Y ? this.getSide() : Direction.UP;
          }
          @Override
          public Direction getHorizontalPlayerFacing() {
            return this.getSide().getAxis() == Direction.Axis.Y ? Direction.NORTH : this.getSide();
          }
          @Override
          public float getPlayerYaw() {
            return this.getSide().asRotation();
          }
        };

        if (((BlockItem) item).place(context) == ActionResult.FAIL) {
          world.playSound(
              null,
              pos.offset(direction.getOpposite()),
              RegSounds.FAIL,
              SoundCategory.BLOCKS,
              1f,
              1f
          );
        } else {
          // Only shows particles floating if block above is not solid
          if (!world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) {
            ParticlePayload.sendParticlePacketToClients(world,
                new ParticlePayload(ParticleTypes.WHITE_SMOKE,
                    pos,
                    new Vec3d(0, 0.65, 0),
                    0.02f,
                    (byte) 10,
                    (byte) 2
                ));
          }
          world.playSound(
              null,
              pos,
              SoundEvents.ENTITY_BREEZE_SHOOT,
              SoundCategory.BLOCKS,
              0.3f,
              1f
          );
        }
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
  protected MapCodec<? extends PlacerBlock> getCodec() {
    return CODEC;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new PlacerBlockEntity(pos, state);
  }
}