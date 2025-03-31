package com.kotori316.testutil;

import com.kotori316.debug.DebugUtils;
import com.kotori316.testutil.common.TestFunctionRegister;
import com.kotori316.testutil.common.TestUtilityCommon;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtility implements ModInitializer {
    @Override
    public void onInitialize() {
        TestUtilityCommon.GENERAL.info("Hello Fabric world!");
        TestFunctionRegister.addFunctionsToRegistry(DebugUtils.MOD_ID, TestFunctionRegister::vanillaTestFunctionRegister);
    }

    @SuppressWarnings("unchecked")
    public static void registerTest(List<?> registries) {
        TestUtilityCommon.GENERAL.info("Registering test");
        Map<ResourceKey<? extends Registry<?>>, Registry<?>> registryMap = new HashMap<>();
        try {
            var clazz = Class.forName("net.minecraft.resources.RegistryDataLoader$Loader");
            var method = clazz.getDeclaredMethod("registry");
            method.setAccessible(true);
            for (Object loader : registries) {
                var r = (Registry<?>) method.invoke(loader);
                registryMap.put(r.key(), r);
            }
        } catch (ReflectiveOperationException e) {
            TestUtilityCommon.GENERAL.error("Failed to register test", e);
            throw new RuntimeException(e);
        }
        var testInstances = (Registry<GameTestInstance>) registryMap.get(Registries.TEST_INSTANCE);
        var testEnvironmentDefinitionRegistry = (Registry<TestEnvironmentDefinition>) registryMap.get(Registries.TEST_ENVIRONMENT);

        var environment = Registry.registerForHolder(testEnvironmentDefinitionRegistry, TestFunctionRegister.TEST_ENVIRONMENT_KEY, new TestEnvironmentDefinition.AllOf());
        TestFunctionRegister.forEach((resourceLocation, testFunction) ->
            Registry.register(testInstances, resourceLocation, testFunction.createTestInstance(environment))
        );
    }
}
