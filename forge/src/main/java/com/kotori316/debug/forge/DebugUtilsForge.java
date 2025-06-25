package com.kotori316.debug.forge;

import com.kotori316.debug.ClientSetting;
import com.kotori316.debug.DebugUtils;
import com.kotori316.debug.ServerSetting;
import com.kotori316.debug.command.CommandRegister;
import com.kotori316.testutil.TestUtilMod;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(DebugUtils.MOD_ID)
public class DebugUtilsForge {

    public DebugUtilsForge(FMLJavaModLoadingContext context) {
        if (FMLLoader.getDist().isClient()) {
            FMLLoadCompleteEvent.getBus(context.getModBusGroup()).addListener(this::loadComplete);
        }
        RegisterCommandsEvent.BUS.addListener(this::registerCommand);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(this::serverLoggedIn);
        TestUtilMod.register(context);
    }

    void registerCommand(RegisterCommandsEvent event) {
        CommandRegister.registerAll(event.getDispatcher(), event.getBuildContext());
    }

    void serverLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        Level level = player.level();
        ServerSetting.onLogin(player, level, false);
    }

    void loadComplete(FMLLoadCompleteEvent event) {
        ClientSetting.onLogin();
    }
}
