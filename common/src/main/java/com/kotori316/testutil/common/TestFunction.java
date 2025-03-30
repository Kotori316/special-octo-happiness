package com.kotori316.testutil.common;

import com.google.common.base.CaseFormat;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public record TestFunction
    (
        ResourceLocation name,
        ResourceLocation structureName,
        int maxTicks,
        int setupTicks,
        Consumer<GameTestHelper> test
    ) {
    public static final String EMPTY_STRUCTURE = "empty";
    public static final String NO_PLACE_STRUCTURE = "no_place";

    public static TestFunction create(String modID, String batch, String name, Consumer<GameTestHelper> test) {
        return createWithStructure(modID, batch, name, EMPTY_STRUCTURE, test);
    }

    public static TestFunction create(String modID, String batch, String name, Runnable test) {
        return createWithStructure(modID, batch, name, NO_PLACE_STRUCTURE, test);
    }

    public static TestFunction createWithStructure(String modID, String batch, String testName, String structureName, Consumer<GameTestHelper> test) {
        return createInternal(modID, batch, testName, structureName, wrapper(test));
    }

    public static TestFunction createWithStructure(String modID, String batch, String testName, String structureName, Runnable test) {
        return createInternal(modID, batch, testName, structureName, wrapper(g -> {
            test.run();
            g.succeed();
        }));
    }

    private static Consumer<GameTestHelper> wrapper(Consumer<GameTestHelper> original) {
        return g -> {
            try {
                original.accept(g);
            } catch (AssertionError assertionError) {
                var e = new RuntimeException(assertionError.getMessage());
                e.addSuppressed(assertionError);
                throw e;
            }
        };
    }

    private static TestFunction createInternal(String modID, String batch, String testName, String structureName, Consumer<GameTestHelper> wrapped) {
        var snakeTestName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, testName);
        var structureLocation = ResourceLocation.parse(structureName);
        return new TestFunction(
            ResourceLocation.fromNamespaceAndPath(modID, snakeTestName),
            structureLocation,
            100,
            1,
            wrapped
        );
    }

    public TestFunction(String modId, String name, Consumer<GameTestHelper> test) {
        this(
            ResourceLocation.fromNamespaceAndPath(modId, name),
            ResourceLocation.parse("minecraft:empty"),
            100,
            1,
            test
        );
    }

    public TestData<Holder<TestEnvironmentDefinition>> createTestData(Holder<TestEnvironmentDefinition> definition) {
        return new TestData<>(definition, structureName, maxTicks, setupTicks, true);
    }

    public GameTestInstance createTestInstance(Holder<TestEnvironmentDefinition> definition) {
        return this.createTestInstance(createTestData(definition));
    }

    public GameTestInstance createTestInstance(TestData<Holder<TestEnvironmentDefinition>> testData) {
        return new TestFunctionGameTestInstance(testData, this);
    }

    private static class TestFunctionGameTestInstance extends GameTestInstance {

        private final TestFunction testFunction;

        TestFunctionGameTestInstance(TestData<Holder<TestEnvironmentDefinition>> testData, TestFunction testFunction) {
            super(testData);
            this.testFunction = testFunction;
        }

        @Override
        public void run(GameTestHelper gameTestHelper) {
            testFunction.test.accept(gameTestHelper);
        }

        @Override
        public MapCodec<? extends GameTestInstance> codec() {
            return MapCodec.unit(this);
        }

        @Override
        protected MutableComponent typeDescription() {
            return Component.literal(testFunction.name.toString());
        }
    }
}
