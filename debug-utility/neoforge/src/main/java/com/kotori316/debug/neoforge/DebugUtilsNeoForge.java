package com.kotori316.debug.neoforge;

import com.kotori316.debug.ClientSetting;
import com.kotori316.debug.DebugUtils;
import com.kotori316.debug.ServerSetting;
import com.kotori316.debug.command.CommandRegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(DebugUtils.MOD_ID)
public class DebugUtilsNeoForge {

    public DebugUtilsNeoForge(IEventBus modBus) {
        if (FMLLoader.getDist().isClient()) {
            modBus.addListener(this::loadComplete);
        }
        var forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(this::registerCommand);
        forgeBus.addListener(this::serverLoggedIn);
    }

    void registerCommand(RegisterCommandsEvent event) {
        CommandRegister.registerAll(event.getDispatcher(), event.getBuildContext());
    }

    void serverLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        ServerSetting.onLogin(player, player.level());
    }

    void loadComplete(FMLLoadCompleteEvent event) {
        ClientSetting.onLogin();
    }
}
