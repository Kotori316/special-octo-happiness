package com.kotori316.testutil;

import com.kotori316.testutil.common.TestUtilityCommon;
import com.kotori316.testutil.common.reporter.ReporterRegister;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class TestUtilMod {

    public static void register(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(TestUtilMod::changeReporter);
    }

    static void changeReporter(RegisterGameTestsEvent event) {
        ReporterRegister.changeReporter();
        TestUtilityCommon.GENERAL.info("Inject CreateFileReporter by {}", TestUtilityCommon.MOD_ID);
    }
}
