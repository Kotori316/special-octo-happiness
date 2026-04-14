package com.kotori316.testutil;

import com.kotori316.testutil.common.TestFunctionRegister;
import com.kotori316.testutil.common.TestUtilityCommon;
import com.kotori316.testutil.common.reporter.ReporterRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.StructureUtils;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.LockHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

public final class TestUtilMod {
    private static final String NO_REGISTRATION_KEY = "TEST_UTILITY_NO_REGISTRATION";
    private static final Logger EVENT_BUS_LOGGER = LoggerFactory.getLogger("TestUtil/EventBus");

    public static void register(IEventBus modBus) {
        TestUtilityCommon.GENERAL.info("Init TestUtilMod. Loader: {}", TestUtilMod.class.getClassLoader());
        modBus.addListener(new ChangeReporter());
        if (Boolean.parseBoolean(System.getenv(NO_REGISTRATION_KEY))) {
            TestUtilityCommon.GENERAL.info("Test registration from {} is disabled", TestUtilityCommon.MOD_ID);
        } else {
            TestUtilityCommon.GENERAL.info("Test registration from {} is enabled", TestUtilityCommon.MOD_ID);
            modBus.addListener(new RegisterTests());
            modBus.addListener(new RegisterTestFunctions());
        }
        logModBusListeners(modBus);
    }

    private static final class ChangeReporter implements Consumer<RegisterGameTestsEvent> {
        @Override
        public void accept(RegisterGameTestsEvent registerGameTestsEvent) {
            ReporterRegister.changeReporter();
            TestUtilityCommon.GENERAL.info("Inject CreateFileReporter by {}", TestUtilityCommon.MOD_ID);
            StructureUtils.testStructuresSourceDir = Path.of("gameteststructures");
        }
    }

    private static final class RegisterTestFunctions implements Consumer<RegisterEvent> {
        @Override
        public void accept(RegisterEvent event) {
            event.register(Registries.TEST_FUNCTION, consumerRegisterHelper ->
                TestFunctionRegister.addFunctionsToRegistry(null, consumerRegisterHelper::register)
            );
        }
    }

    private static final class RegisterTests implements Consumer<RegisterGameTestsEvent> {
        @Override
        public void accept(RegisterGameTestsEvent event) {
            var environmentMap = TestFunctionRegister.testEnvironments(event::registerEnvironment);
            TestFunctionRegister.forEach((Identifier, testFunction) ->
                event.registerTest(Identifier, testFunction.createTestInstance(environmentMap.get(testFunction.environmentName())))
            );
            TestUtilityCommon.TEST_LOADER_LOGGER.info("Registered {} tests for {}", TestFunctionRegister.getTestFunctions().size(), TestUtilityCommon.MOD_ID);
        }
    }


    @SuppressWarnings({"UnstableApiUsage"})
    public static void logModBusListeners(IEventBus modBus) {
        if (!(modBus instanceof EventBus)) {
            return;
        }
        // listeners
        try {
            var field = EventBus.class.getDeclaredField("listeners");
            field.setAccessible(true);
            Map<?, ?> listeners = (Map<?, ?>) field.get(modBus);
            listeners.forEach((key, value) -> EVENT_BUS_LOGGER.debug("Listener: {} -> {}", key, value));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        // listenerLists
        try {
            var field = EventBus.class.getDeclaredField("listenerLists");
            field.setAccessible(true);
            LockHelper<?, ?> listenerLists = (LockHelper<?, ?>) field.get(modBus);
            var f = listenerLists.getClass().getDeclaredField("backingMap");
            f.setAccessible(true);
            Map<?, ?> backingMap = (Map<?, ?>) f.get(listenerLists);
            backingMap.forEach((key, value) -> EVENT_BUS_LOGGER.debug("ListenerList: {} -> {}", key, value));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
