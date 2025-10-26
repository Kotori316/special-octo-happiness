package com.kotori316.testutil.common;

import com.kotori316.debug.DebugUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TestFunctionRegister {
    private static final Map<ResourceLocation, TestFunction> TEST_FUNCTIONS;

    static {
        TEST_FUNCTIONS = new HashMap<>();
        registerTestFunction(TestFunction.create(DebugUtils.MOD_ID, "dummy_test", GameTestHelper::succeed));
    }

    public static void registerTestFunction(TestFunction testFunction) {
        TestUtilityCommon.TEST_LOADER_LOGGER.info("Register {}(batch: {}, structure: {})", testFunction.name(), testFunction.environmentName(), testFunction.structureName());
        TEST_FUNCTIONS.put(testFunction.name(), testFunction);
    }

    @ApiStatus.Internal
    public static void forEach(BiConsumer<ResourceLocation, TestFunction> consumer) {
        TEST_FUNCTIONS.forEach(consumer);
    }

    public static void addFunctionsToRegistry(@Nullable String modId, BiConsumer<ResourceLocation, Consumer<GameTestHelper>> registerFunction) {
        TEST_FUNCTIONS.entrySet().stream()
            .filter(entry -> modId == null || entry.getKey().getNamespace().equals(modId))
            .forEach(e ->
                registerFunction.accept(e.getKey(), e.getValue().test())
            );
    }

    public static void vanillaTestFunctionRegister(ResourceLocation resourceLocation, Consumer<GameTestHelper> test) {
        Registry.register(BuiltInRegistries.TEST_FUNCTION, resourceLocation, test);
    }

    @ApiStatus.Internal
    public static Map<ResourceLocation, Holder<TestEnvironmentDefinition>> testEnvironments(Function<ResourceLocation, Holder<TestEnvironmentDefinition>> registerFunction) {
        return TEST_FUNCTIONS.values().stream()
            .map(TestFunction::environmentName)
            .distinct()
            .collect(Collectors.toMap(Function.identity(), registerFunction));
    }
}
