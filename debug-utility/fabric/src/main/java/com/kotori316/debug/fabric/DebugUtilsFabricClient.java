package com.kotori316.debug.fabric;

import com.kotori316.debug.ClientSetting;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class DebugUtilsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ClientSetting.onLogin());
    }
}
