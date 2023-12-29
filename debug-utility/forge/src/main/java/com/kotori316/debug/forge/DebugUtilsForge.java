package com.kotori316.debug.forge;

import com.kotori316.debug.ClientSetting;
import com.kotori316.debug.DebugUtils;
import com.kotori316.debug.ServerSetting;
import com.kotori316.debug.command.CommandRegister;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DebugUtils.MOD_ID)
public class DebugUtilsForge {

    public DebugUtilsForge() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::clientInit);
        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::registerCommand);
        forgeBus.addListener(this::serverLoggedIn);
    }

     void clientInit(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(this::clientLoggedIn);
    }

    void registerCommand(RegisterCommandsEvent event) {
        CommandRegister.registerAll(event.getDispatcher(), event.getBuildContext());
    }

    void serverLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        var player = event.getEntity();
        ServerSetting.onLogin(player, player.level());
    }

    void clientLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ClientSetting.onLogin();
    }
}
