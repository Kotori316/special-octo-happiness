package com.kotori316.test_utility.client.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.test_utility.TestUtility;

@Mixin(Minecraft.class)
public abstract class DisableAuthMixin {

    @Inject(method = "createUserApiService", at = @At("HEAD"), cancellable = true)
    private void bypassAuth(YggdrasilAuthenticationService yggdrasilAuthenticationService, GameConfig gameConfig, CallbackInfoReturnable<UserApiService> cir) {
        TestUtility.LOGGER.info("Bypass Minecraft auth by {}", TestUtility.modId);
        cir.setReturnValue(UserApiService.OFFLINE);
    }
}
