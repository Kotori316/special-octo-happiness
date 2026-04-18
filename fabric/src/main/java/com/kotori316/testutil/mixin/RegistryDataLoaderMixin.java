package com.kotori316.testutil.mixin;

import com.kotori316.testutil.TestUtility;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main logic is from {@link net.fabricmc.fabric.mixin.gametest.RegistryDataLoaderMixin}
 */
@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {
    @Unique
    private static final AtomicBoolean TEST_UTILITY_LOADING_DYNAMIC_REGISTRIES = new AtomicBoolean(false);

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private static void loadFromResources(ResourceManager resourceManager, List<HolderLookup.RegistryLookup<?>> registries, List<RegistryDataLoader.RegistryData<?>> entries, Executor executor, CallbackInfoReturnable<RegistryAccess.Frozen> cir) {
        var keys = entries.stream().map(RegistryDataLoader.RegistryData::key).toList();
        TEST_UTILITY_LOADING_DYNAMIC_REGISTRIES.set(
            keys.contains(Registries.TEST_INSTANCE)
        );
    }

    @Inject(
        method = "lambda$load$2(Ljava/util/List;Ljava/util/Map;Ljava/lang/Void;)Lnet/minecraft/core/RegistryAccess$Frozen;",
        at = @At(value = "HEAD")
    )
    private static void beforeFreeze(List<RegistryLoadTask<?>> loadTasks, Map<ResourceKey<?>, Exception> loadingErrors, Void ignored, CallbackInfoReturnable<RegistryAccess.Frozen> cir) {
        if (TEST_UTILITY_LOADING_DYNAMIC_REGISTRIES.getAndSet(false)) {
            TestUtility.registerTest(loadTasks);
        }
    }
}
