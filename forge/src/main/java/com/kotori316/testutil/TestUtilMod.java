package com.kotori316.testutil;

import com.kotori316.testutil.common.TestFunctionRegister;
import com.kotori316.testutil.common.TestUtilityCommon;
import com.kotori316.testutil.common.reporter.ReporterRegister;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class TestUtilMod {

    public static void register(FMLJavaModLoadingContext context) {
        // context.getModEventBus().addListener(TestUtilMod::changeReporter);
        MinecraftForge.EVENT_BUS.addListener(TestUtilMod::handleServerStartToRegisterTests);
    }

    static void changeReporter() {
        ReporterRegister.changeReporter();
        TestUtilityCommon.GENERAL.info("Inject CreateFileReporter by {}", TestUtilityCommon.MOD_ID);
    }

    static void handleServerStartToRegisterTests(ServerStartingEvent event) {
        var server = event.getServer();
        if (server instanceof GameTestServer) {
            return;
        }
        changeReporter();

        var environments = (WritableRegistry<TestEnvironmentDefinition>) server.registryAccess().lookupOrThrow(Registries.TEST_ENVIRONMENT);
        var tests = (WritableRegistry<GameTestInstance>) server.registryAccess().lookupOrThrow(Registries.TEST_INSTANCE);
        var testEnvironment = environments.register(ResourceKey.create(Registries.TEST_ENVIRONMENT, TestFunctionRegister.TEST_ENVIRONMENT_KEY), new TestEnvironmentDefinition.AllOf(), RegistrationInfo.BUILT_IN);
        TestFunctionRegister.forEach((resourceLocation, testFunction) ->
            tests.register(ResourceKey.create(Registries.TEST_INSTANCE, resourceLocation), testFunction.createTestInstance(testEnvironment), RegistrationInfo.BUILT_IN)
        );
    }
}
