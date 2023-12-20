package com.kotori316.testutil;

import com.kotori316.testutil.reporter.ReporterRegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TestUtilMod.MOD_ID)
public final class TestUtilMod {
    public static final String MOD_ID = "test_utility";
    public static final Logger DATA_GENERATOR_LOGGER = LoggerFactory.getLogger("TestUtil/DataGen");
    public static final Logger TEST_LOADER_LOGGER = LoggerFactory.getLogger("TestUtil/TestLoad");

    public TestUtilMod(IEventBus modBus) {
        modBus.addListener(this::changeReporter);
    }

    void changeReporter(RegisterGameTestsEvent event){
        ReporterRegister.changeReporter();
    }
}
