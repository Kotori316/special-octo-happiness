package com.kotori316.debug.fabric;

import com.kotori316.debug.ServerSetting;
import com.kotori316.debug.command.CommandRegister;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class DebugUtilsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            CommandRegister.registerAll(dispatcher, registryAccess)
        );
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
            ServerSetting.onLogin(server, null, false)
        );
    }
}
