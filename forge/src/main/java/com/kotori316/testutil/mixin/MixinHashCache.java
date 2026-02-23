package com.kotori316.testutil.mixin;

import net.minecraft.data.HashCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

@Mixin(HashCache.class)
public final class MixinHashCache {
    @Final
    @Shadow
    private Path rootDir;

    @Inject(method = "purgeStaleAndWrite", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addMetaInfToIgnoreSet(CallbackInfo ci, HashSet<Path> set) {
        try (var stream = Files.walk(rootDir.resolve("META-INF"))) {
            stream.forEach(set::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
