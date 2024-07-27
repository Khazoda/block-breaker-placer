package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.BreakerPlacer;
import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.screen.BreakerScreenHandler;
import com.khazoda.breakerplacer.screen.PlacerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RScreenHandler {

  public static final ScreenHandlerType<PlacerScreenHandler> PLACER_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(PlacerScreenHandler::new, BlockPos.PACKET_CODEC.cast());
  public static final ScreenHandlerType<BreakerScreenHandler> BREAKER_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(BreakerScreenHandler::new, BlockPos.PACKET_CODEC.cast());

  public static void init() {
    Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Constants.NAMESPACE,"placer_screen_handler"), PLACER_SCREEN_HANDLER);
    Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Constants.NAMESPACE,"breaker_screen_handler"), BREAKER_SCREEN_HANDLER);

    BreakerPlacer.loadedRegistries += 1;
  }
}
