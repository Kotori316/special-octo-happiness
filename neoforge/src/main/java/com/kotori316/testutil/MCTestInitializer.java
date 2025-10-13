package com.kotori316.testutil;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.neoforged.neoforge.capabilities.CapabilityHooks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.GameData;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.support.ReflectionSupport;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class MCTestInitializer implements BeforeAllCallback {
    public static <T> Consumer<T> empty() {
        return o -> {
        };
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        setUp(() -> {
        });
    }

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    public static void setUp(Runnable additional) {
        setUp(additional, empty());
    }

    public static synchronized void setUp(Runnable additional, Consumer<RegisterCapabilitiesEvent> modCapabilityRegister) {
        if (!INITIALIZED.getAndSet(true)) {
            resolveInfoCmpError();
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            unfreezeGameData();
            additional.run();
            registerCapabilities(modCapabilityRegister.andThen(registerNeoForgeCapabilities()));
        }
    }

    private static void resolveInfoCmpError() {
        final var name = Terminal.TYPE_DUMB_COLOR;
        InfoCmp.setDefaultInfoCmp(name, () ->
            Try.call(() -> InfoCmp.class.getDeclaredMethod("loadDefaultInfoCmp", String.class))
                // Setting name is dumb-color, but file name is dumb-colors
                .andThenTry(m -> ReflectionSupport.invokeMethod(m, null, name + "s"))
                .andThenTry(String.class::cast)
                .getOrThrow(RuntimeException::new)
        );
    }

    /**
     * Copied from {@link GameData#unfreezeData()} to avoid caller check which is to be installed in {@link GameData} class.
     */
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    private static void unfreezeGameData() {
        BuiltInRegistries.REGISTRY.stream().filter(r -> r instanceof MappedRegistry).forEach(r -> ((MappedRegistry<?>) r).unfreeze(false));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static Consumer<RegisterCapabilitiesEvent> registerNeoForgeCapabilities() {
        return CapabilityHooks::registerVanillaProviders;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void registerCapabilities(Consumer<RegisterCapabilitiesEvent> registerFunction) {
        try {
            var initializedField = CapabilityHooks.class.getDeclaredField("initialized");
            initializedField.setAccessible(true);
            if (!initializedField.getBoolean(null)) {
                var constructor = RegisterCapabilitiesEvent.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                var event = constructor.newInstance();
                registerFunction.accept(event);
            }
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
