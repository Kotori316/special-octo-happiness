package com.kotori316.testutil;

import com.kotori316.testutil.reporter.ReporterRegister;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TestUtilMod.MOD_ID)
public final class TestUtilMod {
    public static final String MOD_ID = "test_utility";
    public static final Logger DATA_GENERATOR_LOGGER = LoggerFactory.getLogger("TestUtil/DataGen");
    public static final Logger TEST_LOADER_LOGGER = LoggerFactory.getLogger("TestUtil/TestLoad");

    public TestUtilMod () {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::changeReporter);
    }

    void changeReporter(RegisterGameTestsEvent event){
        ReporterRegister.changeReporter();
    }
}
