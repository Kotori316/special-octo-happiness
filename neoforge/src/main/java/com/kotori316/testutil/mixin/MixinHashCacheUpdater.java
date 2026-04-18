package com.kotori316.testutil.mixin;

import com.google.common.hash.HashCode;
import com.kotori316.testutil.common.TestUtilityCommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(targets = "net.minecraft.data.HashCache$CacheUpdater")
public final class MixinHashCacheUpdater {
    @Inject(method = "writeIfNeeded", at = @At("HEAD"))
    private void outputPath(Path path, byte[] input, HashCode hash, CallbackInfo ci) {
        TestUtilityCommon.logDataGeneration(path, hash);
    }
}
