package com.kotori316.testutil.common;

import com.kotori316.debug.DebugUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class TestFunctionRegister {
    public static final ResourceLocation TEST_ENVIRONMENT_KEY = ResourceLocation.fromNamespaceAndPath(DebugUtils.MOD_ID, "test_environment");
    private static final Map<ResourceLocation, TestFunction> TEST_FUNCTIONS;

    static {
        TEST_FUNCTIONS = new HashMap<>();
        registerTestFunction(new TestFunction(DebugUtils.MOD_ID, "dummy_test", GameTestHelper::succeed));
    }

    public static void registerTestFunction(TestFunction testFunction) {
        TEST_FUNCTIONS.put(testFunction.name(), testFunction);
    }

    public static void forEach(BiConsumer<ResourceLocation, TestFunction> consumer) {
        TEST_FUNCTIONS.forEach(consumer);
    }

    public static void addFunctionsToRegistry(String modId) {
        TEST_FUNCTIONS.entrySet().stream()
            .filter(entry -> entry.getKey().getNamespace().equals(modId))
            .forEach(e ->
                Registry.register(BuiltInRegistries.TEST_FUNCTION, e.getKey(), e.getValue().test())
            );
    }
}
