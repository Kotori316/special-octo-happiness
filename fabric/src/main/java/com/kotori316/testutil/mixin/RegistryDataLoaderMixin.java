package com.kotori316.testutil.mixin;

import com.kotori316.testutil.TestUtility;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main logic is from {@link net.fabricmc.fabric.mixin.gametest.RegistryLoaderMixin}
 */
@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {
    @Unique
    private static final AtomicBoolean TEST_UTILITY_LOADING_DYNAMIC_REGISTRIES = new AtomicBoolean(false);

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;", at = @At("HEAD"))
    private static void loadFromResources(ResourceManager resourceManager, List<HolderLookup.RegistryLookup<?>> registryLookups, List<RegistryDataLoader.RegistryData<?>> registryData, CallbackInfoReturnable<RegistryAccess.Frozen> cir) {
        var keys = registryData.stream().map(RegistryDataLoader.RegistryData::key).toList();
        TEST_UTILITY_LOADING_DYNAMIC_REGISTRIES.set(
            keys.contains(Registries.TEST_INSTANCE)
        );
    }

    @Inject(method = "load(Lnet/minecraft/resources/RegistryDataLoader$LoadingFunction;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 1))
    private static void beforeFreeze(@Coerce Object loadingFunction, List<HolderLookup.RegistryLookup<?>> registryLookups, List<RegistryDataLoader.RegistryData<?>> registryData, CallbackInfoReturnable<RegistryAccess.Frozen> cir,
                                     @Local(ordinal = 2) @Coerce List<?> registriesList) {
        if (TEST_UTILITY_LOADING_DYNAMIC_REGISTRIES.getAndSet(false)) {
            TestUtility.registerTest(registriesList);
        }
    }
}
