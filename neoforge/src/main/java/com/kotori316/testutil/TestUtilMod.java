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
    private static final String NO_REGISTRATION_KEY = "TEST_UTILITY_NO_REGISTRATION";

    public static void register(IEventBus modBus) {
        modBus.addListener(TestUtilMod::changeReporter);
        if (Boolean.parseBoolean(System.getenv(NO_REGISTRATION_KEY))) {
            TestUtilityCommon.GENERAL.info("Test registration from {} is disabled", TestUtilityCommon.MOD_ID);
        } else {
            TestUtilityCommon.GENERAL.info("Test registration from {} is enabled", TestUtilityCommon.MOD_ID);
            modBus.addListener(TestUtilMod::registerTests);
            modBus.addListener(TestUtilMod::registerTestFunctions);
        }
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
            event.registerTest(resourceLocation, testFunction.createTestInstance(environmentMap.get(testFunction.environmentName())))
        );
        TestUtilityCommon.TEST_LOADER_LOGGER.info("Registered {} tests for {}", TestFunctionRegister.getTestFunctions().size(), TestUtilityCommon.MOD_ID);
    }
}
