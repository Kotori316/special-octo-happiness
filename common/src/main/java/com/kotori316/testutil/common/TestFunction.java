package com.kotori316.testutil.common;

import com.google.common.base.CaseFormat;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Don't instantiate this class directly.
 *
 * @param environmentName the same meaning as batch name, but for 1.21.5+
 */
public record TestFunction
    (
        ResourceLocation name,
        ResourceLocation structureName,
        ResourceLocation environmentName,
        int maxTicks,
        int setupTicks,
        Consumer<GameTestHelper> test
    ) {
    public static final String EMPTY_STRUCTURE = "empty";
    public static final String NO_PLACE_STRUCTURE = "no_place";

    /**
     * Use {@code "${modId}:test"} as environment name.
     */
    public static TestFunction create(String modId, String name, Consumer<GameTestHelper> test) {
        return create(modId, modId + ":test", name, test);
    }

    public static TestFunction create(String modID, String batch, String name, Consumer<GameTestHelper> test) {
        return createWithStructure(modID, batch, name, EMPTY_STRUCTURE, test);
    }

    public static TestFunction create(String modID, String batch, String name, Runnable test) {
        return createWithStructure(modID, batch, name, NO_PLACE_STRUCTURE, test);
    }

    public static TestFunction createWithStructure(String modID, String batch, String testName, String structureName, Consumer<GameTestHelper> test) {
        return createInternal(modID, batch, testName, structureName, wrapper(testName, test));
    }

    public static TestFunction createWithStructure(String modID, String batch, String testName, String structureName, Runnable test) {
        return createInternal(modID, batch, testName, structureName, wrapper(testName, g -> {
            test.run();
            g.succeed();
        }));
    }

    private static Consumer<GameTestHelper> wrapper(String testName, Consumer<GameTestHelper> original) {
        return g -> {
            try {
                original.accept(g);
            } catch (AssertionError assertionError) {
                var e = new TestFunctionException(testName, assertionError.getMessage());
                e.addSuppressed(assertionError);
                throw e;
            }
        };
    }

    private static TestFunction createInternal(String modID, String batch, String testName, String structureName, Consumer<GameTestHelper> wrapped) {
        var snakeTestName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, testName);
        var structureLocation = ResourceLocation.parse(structureName);
        return TestFunction.createRaw(
            ResourceLocation.fromNamespaceAndPath(modID, snakeTestName),
            structureLocation,
            ResourceLocation.parse(batch),
            100,
            1,
            wrapped
        );
    }

    public static TestFunction createRaw(
        ResourceLocation name,
        ResourceLocation structureName,
        ResourceLocation environmentName,
        int maxTicks,
        int setupTicks,
        Consumer<GameTestHelper> test
    ) {
        return new TestFunction(name, structureName, environmentName, maxTicks, setupTicks, test);
    }

    public TestData<Holder<TestEnvironmentDefinition>> createTestData(Holder<TestEnvironmentDefinition> definition) {
        return new TestData<>(definition, structureName, maxTicks, setupTicks, true);
    }

    public GameTestInstance createTestInstance(Holder<TestEnvironmentDefinition> definition) {
        return this.createTestInstance(createTestData(definition));
    }

    public GameTestInstance createTestInstance(TestData<Holder<TestEnvironmentDefinition>> testData) {
        var functionKey = ResourceKey.create(Registries.TEST_FUNCTION, this.name);
        return new FunctionGameTestInstance(functionKey, testData);
    }
}
