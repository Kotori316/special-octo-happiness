package com.kotori316.testutil;

import com.kotori316.debug.DebugUtils;
import com.kotori316.testutil.common.TestFunctionRegister;
import com.kotori316.testutil.common.TestUtilityCommon;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.RegistryLoadTask;
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
    public static void registerTest(List<RegistryLoadTask<?>> registries) {
        TestUtilityCommon.GENERAL.info("Registering test");
        Map<ResourceKey<? extends Registry<?>>, Registry<?>> registryMap = new HashMap<>();
        for (RegistryLoadTask<?> entry : registries) {
            try {
                var field = RegistryLoadTask.class.getDeclaredField("registry");
                field.setAccessible(true);
                Registry<?> registry = (Registry<?>) field.get(entry);
                registryMap.put(registry.key(), registry);
            } catch (ReflectiveOperationException e) {
                TestUtilityCommon.GENERAL.error("Failed to register test", e);
                throw new RuntimeException(e);
            }
        }
        var testInstances = (Registry<GameTestInstance>) registryMap.get(Registries.TEST_INSTANCE);
        var testEnvironmentDefinitionRegistry = (Registry<TestEnvironmentDefinition<?>>) registryMap.get(Registries.TEST_ENVIRONMENT);

        var environmentMap = TestFunctionRegister.testEnvironments(r -> Registry.registerForHolder(testEnvironmentDefinitionRegistry, r, new TestEnvironmentDefinition.AllOf()));
        TestFunctionRegister.forEach((Identifier, testFunction) ->
            Registry.register(testInstances, Identifier, testFunction.createTestInstance(environmentMap.get(testFunction.environmentName())))
        );
    }
}
