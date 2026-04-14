package com.kotori316.testutil.common;

import com.kotori316.debug.DebugUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TestFunctionRegister {
    private static final Map<Identifier, TestFunction> TEST_FUNCTIONS;

    static {
        TEST_FUNCTIONS = new HashMap<>();
        registerTestFunction(TestFunction.create(DebugUtils.MOD_ID, "dummy_test", GameTestHelper::succeed));
    }

    public static synchronized void registerTestFunction(TestFunction testFunction) {
        TestUtilityCommon.logTestName(testFunction);
        TEST_FUNCTIONS.put(testFunction.name(), testFunction);
    }

    public static synchronized Map<Identifier, TestFunction> getTestFunctions() {
        return Collections.unmodifiableMap(TEST_FUNCTIONS);
    }

    @ApiStatus.Internal
    public static synchronized void forEach(BiConsumer<Identifier, TestFunction> consumer) {
        TEST_FUNCTIONS.forEach(consumer);
    }

    public static synchronized void addFunctionsToRegistry(@Nullable String modId, BiConsumer<Identifier, Consumer<GameTestHelper>> registerFunction) {
        TestUtilityCommon.TEST_LOADER_LOGGER.info("Registering test functions for {}", modId);
        TEST_FUNCTIONS.entrySet().stream()
            .filter(entry -> modId == null || entry.getKey().getNamespace().equals(modId))
            .forEach(e ->
                registerFunction.accept(e.getKey(), e.getValue().test())
            );
        TestUtilityCommon.TEST_LOADER_LOGGER.info("Finished registering {} test functions for {}", TEST_FUNCTIONS.size(), modId);
    }

    @ApiStatus.Internal
    public static void vanillaTestFunctionRegister(Identifier Identifier, Consumer<GameTestHelper> test) {
        Registry.register(BuiltInRegistries.TEST_FUNCTION, Identifier, test);
    }

    public static synchronized Map<Identifier, Holder<TestEnvironmentDefinition<?>>> testEnvironments(Function<Identifier, Holder<TestEnvironmentDefinition<?>>> registerFunction) {
        TestUtilityCommon.TEST_LOADER_LOGGER.info("Registering test environments");
        return TEST_FUNCTIONS.values().stream()
            .map(TestFunction::environmentName)
            .distinct()
            .collect(Collectors.toMap(Function.identity(), registerFunction));
    }
}
