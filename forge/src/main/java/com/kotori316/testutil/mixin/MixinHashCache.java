package com.kotori316.testutil.mixin;

import com.kotori316.testutil.common.HashCacheIgnore;
import net.minecraft.data.HashCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.util.Set;

@SuppressWarnings("InjectLocalCaptureCanBeReplacedWithLocal") // No MixinExtra
@Mixin(HashCache.class)
public final class MixinHashCache {
    @Final
    @Shadow
    private Path rootDir;

    @Inject(method = "purgeStaleAndWrite", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addMetaInfToIgnoreSet(CallbackInfo ci, Set<Path> allowedFiles) {
        HashCacheIgnore.addMetaInfToIgnoreSet(rootDir, allowedFiles);
    }
}
