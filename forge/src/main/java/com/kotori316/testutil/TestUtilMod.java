package com.kotori316.testutil;

import com.kotori316.testutil.common.TestFunctionRegister;
import com.kotori316.testutil.common.TestUtilityCommon;
import com.kotori316.testutil.common.reporter.ReporterRegister;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

public final class TestUtilMod {

    public static void register(FMLJavaModLoadingContext context) {
        // context.getModEventBus().addListener(TestUtilMod::changeReporter);
        context.getModEventBus().addListener(TestUtilMod::registerTestFunctions);
        MinecraftForge.EVENT_BUS.addListener(TestUtilMod::handleServerStartToRegisterTests);
    }

    static void changeReporter() {
        ReporterRegister.changeReporter();
        TestUtilityCommon.GENERAL.info("Inject CreateFileReporter by {}", TestUtilityCommon.MOD_ID);
    }

    static void registerTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, consumerRegisterHelper ->
            TestFunctionRegister.addFunctionsToRegistry(null, consumerRegisterHelper::register)
        );
    }

    static void handleServerStartToRegisterTests(ServerStartingEvent event) {
        var server = event.getServer();
        if (server instanceof GameTestServer) {
            return;
        }
        changeReporter();

        var environments = (MappedRegistry<TestEnvironmentDefinition>) server.registryAccess().lookupOrThrow(Registries.TEST_ENVIRONMENT);
        var tests = (MappedRegistry<GameTestInstance>) server.registryAccess().lookupOrThrow(Registries.TEST_INSTANCE);
        environments.unfreeze();
        tests.unfreeze();

        var testEnvironment = environments.register(ResourceKey.create(Registries.TEST_ENVIRONMENT, TestFunctionRegister.TEST_ENVIRONMENT_KEY), new TestEnvironmentDefinition.AllOf(), RegistrationInfo.BUILT_IN);
        TestFunctionRegister.forEach((resourceLocation, testFunction) ->
            tests.register(ResourceKey.create(Registries.TEST_INSTANCE, resourceLocation), testFunction.createTestInstance(testEnvironment), RegistrationInfo.BUILT_IN)
        );
        environments.freeze();
        tests.freeze();
    }
}
