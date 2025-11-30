package com.kotori316.testutil;

import com.kotori316.testutil.common.TestFunctionRegister;
import com.kotori316.testutil.common.TestUtilityCommon;
import com.kotori316.testutil.common.reporter.ReporterRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.StructureUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.nio.file.Path;

public final class TestUtilMod {
    public static void register(IEventBus modBus) {
        modBus.addListener(TestUtilMod::changeReporter);
        modBus.addListener(TestUtilMod::registerTests);
        modBus.addListener(TestUtilMod::registerTestFunctions);
    }

    static void changeReporter(RegisterGameTestsEvent event) {
        ReporterRegister.changeReporter();
        TestUtilityCommon.GENERAL.info("Inject CreateFileReporter by {}", TestUtilityCommon.MOD_ID);
        StructureUtils.testStructuresDir = Path.of("gameteststructures");
    }

    static void registerTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, consumerRegisterHelper ->
            TestFunctionRegister.addFunctionsToRegistry(null, consumerRegisterHelper::register)
        );
    }

    static void registerTests(RegisterGameTestsEvent event) {
        var environmentMap = TestFunctionRegister.testEnvironments(event::registerEnvironment);
        TestFunctionRegister.forEach((resourceLocation, testFunction) ->
                event.registerTest(resourceLocation, testFunction.createTestInstance(environmentMap.get(testFunction.environmentName()))),
            map -> TestUtilityCommon.TEST_LOADER_LOGGER.info("Registered {} tests for {}", map.size(), TestUtilityCommon.MOD_ID)
        );
    }
}
