package com.kotori316.debug;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class ClientSetting {
    public static void onLogin() {
        var settings = Minecraft.getInstance().options;
        if (settings.cloudStatus().get() != CloudStatus.OFF) {
            settings.autoJump().set(false);
            settings.bobView().set(false);
            settings.entityShadows().set(false);
            settings.attackIndicator().set(AttackIndicatorStatus.HOTBAR);
            settings.cloudStatus().set(CloudStatus.OFF);

            settings.pauseOnLostFocus = false;
            settings.advancedItemTooltips = true;
            settings.keySprint.setKey(InputConstants.getKey("key.keyboard.q"));
            settings.keyDrop.setKey(InputConstants.getKey("key.keyboard.right.bracket"));

            settings.getSoundSourceOptionInstance(SoundSource.MASTER).set(0.25);
            settings.getSoundSourceOptionInstance(SoundSource.MUSIC).set(0.125);
        }
    }
}
