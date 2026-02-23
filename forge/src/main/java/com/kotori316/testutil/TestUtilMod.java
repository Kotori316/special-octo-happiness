package com.kotori316.testutil;

import com.kotori316.testutil.common.TestFunctionRegister;
import com.kotori316.testutil.common.TestUtilityCommon;
import com.kotori316.testutil.common.reporter.ReporterRegister;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.nio.file.Path;

public final class TestUtilMod {

    public static void register(FMLJavaModLoadingContext context) {
        RegisterEvent.getBus(context.getModBusGroup()).addListener(TestUtilMod::registerTestFunctions);
        ServerStartingEvent.BUS.addListener(TestUtilMod::handleServerStartToRegisterTests);
    }

    static void changeReporter() {
        ReporterRegister.changeReporter();
        TestUtilityCommon.GENERAL.info("Inject CreateFileReporter by {}", TestUtilityCommon.MOD_ID);
        StructureUtils.testStructuresDir = Path.of("gameteststructures");
    }

    static void registerTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, consumerRegisterHelper ->
            TestFunctionRegister.addFunctionsToRegistry(null, consumerRegisterHelper::register)
        );
    }

    @SuppressWarnings("deprecation")
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

        var testEnvironmentMap = TestFunctionRegister.testEnvironments(r -> environments.register(ResourceKey.create(Registries.TEST_ENVIRONMENT, r), new TestEnvironmentDefinition.AllOf(), RegistrationInfo.BUILT_IN));
        TestFunctionRegister.forEach((Identifier, testFunction) ->
            tests.register(ResourceKey.create(Registries.TEST_INSTANCE, Identifier), testFunction.createTestInstance(testEnvironmentMap.get(testFunction.environmentName())), RegistrationInfo.BUILT_IN)
        );
        environments.freeze();
        tests.freeze();
    }
}
