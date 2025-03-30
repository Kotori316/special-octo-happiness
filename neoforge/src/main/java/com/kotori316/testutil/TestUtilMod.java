package com.kotori316.testutil;

import com.kotori316.testutil.common.TestFunctionRegister;
import com.kotori316.testutil.common.TestUtilityCommon;
import com.kotori316.testutil.common.reporter.ReporterRegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

public final class TestUtilMod {
    public static void register(IEventBus modBus) {
        modBus.addListener(TestUtilMod::changeReporter);
        modBus.addListener(TestUtilMod::registerTests);
    }

    static void changeReporter(RegisterGameTestsEvent event) {
        ReporterRegister.changeReporter();
        TestUtilityCommon.GENERAL.info("Inject CreateFileReporter by {}", TestUtilityCommon.MOD_ID);
    }

    static void registerTests(RegisterGameTestsEvent event) {
        var testEnvironment = event.registerEnvironment(TestFunctionRegister.TEST_ENVIRONMENT_KEY);
        TestFunctionRegister.forEach((resourceLocation, testFunction) ->
            event.registerTest(resourceLocation, testFunction.createTestInstance(testEnvironment))
        );
    }
}
