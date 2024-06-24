package com.kotori316.testutil;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.capabilities.CapabilityHooks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.server.LanguageHook;
import net.neoforged.neoforgespi.language.IConfigurable;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.IModLanguageLoader;
import net.neoforged.neoforgespi.locating.ForgeFeature;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.Nullable;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.support.ReflectionSupport;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public final class MCTestInitializer implements BeforeAllCallback {
    public static <T> Consumer<T> empty() {
        return o -> {
        };
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        setUp(Objects.requireNonNullElse(System.getenv("target_mod"), "none"), () -> {
        });
    }

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    public static void setUp(String modId, Runnable additional) {
        setUp(modId, additional, empty(), empty());
    }

    public static synchronized void setUp(String modId, Runnable additional, Consumer<RegisterEvent> modResourceRegister, Consumer<RegisterCapabilitiesEvent> modCapabilityRegister) {
        if (!INITIALIZED.getAndSet(true)) {
            resolveInfoCmpError();
            // unfreezeGameData();
            additional.run();
            /*changeDist();
            SharedConstants.tryDetectVersion();
            setHandler();
            Bootstrap.bootStrap();
            ModLoadingContext.get().setActiveContainer(new DummyModContainer(modId));
            mockCapability();
            mockRegistries();
            setFluidType();
            setLanguage(modId);
            activateRegistry();
            registerModObjects(modResourceRegister.andThen(registerForgeObjects()));
            registerCapabilities(modCapabilityRegister.andThen(registerNeoForgeCapabilities())); */
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

    private static void changeDist() {
        try {
            Field dist = FMLLoader.class.getDeclaredField("dist");
            dist.setAccessible(true);
            dist.set(null, Dist.CLIENT);
        } catch (Exception e) {
            fail(e);
        }
    }

    private static void setHandler() {
        try {
            Field handler = FMLLoader.class.getDeclaredField("commonLaunchHandler");
            handler.setAccessible(true);
            Constructor<?> launchHandlerConstructor =
                Class.forName("net.neoforged.fml.loading.targets.NeoForgeDataUserdevLaunchHandler")
                    .getConstructor();
            launchHandlerConstructor.setAccessible(true);
            handler.set(null, launchHandlerConstructor.newInstance());
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * Copied from {@link GameData#unfreezeData()} to avoid caller check which is to be installed in {@link GameData} class.
     */
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    private static void unfreezeGameData() {
        BuiltInRegistries.REGISTRY.stream().filter(r -> r instanceof MappedRegistry).forEach(r -> ((MappedRegistry<?>) r).unfreeze());
    }

    private static void mockCapability() {
        /*try {
            var method = CapabilityManager.class.getDeclaredMethod("get", String.class, boolean.class);
            method.setAccessible(true);
            var cap_IEnergyStorage = (Capability<IEnergyStorage>) method.invoke(CapabilityManager.INSTANCE, "IEnergyStorage", false);
            var cap_IFluidHandler = (Capability<IFluidHandler>) method.invoke(CapabilityManager.INSTANCE, "IFluidHandler", false);
            var cap_IFluidHandlerItem = (Capability<IFluidHandlerItem>) method.invoke(CapabilityManager.INSTANCE, "IFluidHandlerItem", false);
            var cap_IItemHandler = (Capability<IItemHandler>) method.invoke(CapabilityManager.INSTANCE, "IItemHandler", false);
            try (var mocked = mockStatic(CapabilityManager.class)) {
                mocked.when(() -> CapabilityManager.get(any()))
                    .thenReturn(cap_IEnergyStorage)
                    .thenReturn(cap_IFluidHandler)
                    .thenReturn(cap_IFluidHandlerItem)
                    .thenReturn(cap_IItemHandler);
                assertEquals(cap_IEnergyStorage, Capabilities.ENERGY);
                assertEquals(cap_IFluidHandler, Capabilities.FLUID_HANDLER);
                assertEquals(cap_IFluidHandlerItem, Capabilities.FLUID_HANDLER_ITEM);
                assertEquals(cap_IItemHandler, Capabilities.ITEM_HANDLER);
            }
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
        assertNotNull(Capabilities.ENERGY);
        assertNotNull(Capabilities.FLUID_HANDLER);
        assertNotNull(Capabilities.FLUID_HANDLER_ITEM);
        assertNotNull(Capabilities.ITEM_HANDLER);*/
    }

    private static void mockRegistries() {
        /*try {
            mockRegistry(ForgeRegistries.ITEMS, ForgeRegistries.class.getDeclaredField("ITEMS"));
            mockRegistry(ForgeRegistries.BLOCKS, ForgeRegistries.class.getDeclaredField("BLOCKS"));
            mockRegistry(ForgeRegistries.FLUIDS, ForgeRegistries.class.getDeclaredField("FLUIDS"));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }*/
    }

   /* @SuppressWarnings({"unchecked", "deprecation", "UnstableApiUsage"})
    private static <T> void mockRegistry(IForgeRegistry<T> registry, Field field) throws ReflectiveOperationException {
        var wrapperGetter = ForgeRegistry.class.getDeclaredMethod("getWrapper");
        wrapperGetter.setAccessible(true);
        var wrapper = (Registry<T>) wrapperGetter.invoke(registry);

        var s = spy(registry);
        when(s.getDelegate((T) any())) // Return: Optional<Holder.Reference<V>>
            .thenAnswer(invocation -> {
                T arg = invocation.getArgument(0);
                return Optional.of(Holder.Reference.createIntrusive(wrapper.asLookup(), arg));
            });

        var theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        var unsafe = (Unsafe) theUnsafe.get(null);
        unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), s);
    }*/

    private static void setFluidType() {
        var airType = new FluidType(FluidType.Properties.create()
            .descriptionId("block.minecraft.air")
            .motionScale(1D)
            .canPushEntity(false)
            .canSwim(false)
            .canDrown(false)
            .fallDistanceModifier(1F)
            .pathType(null)
            .adjacentPathType(null)
            .density(0)
            .temperature(0)
            .viscosity(0));
        var waterType = new FluidType(FluidType.Properties.create()
            .descriptionId("block.minecraft.water")
            .fallDistanceModifier(0F)
            .canExtinguish(true)
            .canConvertToSource(true)
            .supportsBoating(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
            .canHydrate(true));
        var lavaType = new FluidType(FluidType.Properties.create()
            .descriptionId("block.minecraft.lava")
            .canSwim(false)
            .canDrown(false)
            .pathType(PathType.LAVA)
            .adjacentPathType(null)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
            .lightLevel(15)
            .density(3000)
            .viscosity(6000)
            .temperature(1300));
        try {
            var field = Fluid.class.getDeclaredField("forgeFluidType");
            // UnsafeHacks.setField(field, Fluids.EMPTY, airType);
            // UnsafeHacks.setField(field, Fluids.WATER, waterType);
            // UnsafeHacks.setField(field, Fluids.LAVA, lavaType);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    private static void setLanguage(String modId) {
        LanguageHook.loadBuiltinLanguages();
        try (var langStream = MCTestInitializer.class.getResourceAsStream("/assets/%s/lang/en_us.json".formatted(modId))) {
            if (langStream == null) {
                System.out.println("(MCTestInitializer#setLanguage) The lang file is null");
                return;
            }
            Try.call(() -> LanguageHook.class.getDeclaredField("modTable"))
                .andThenTry(f -> {
                    f.setAccessible(true);
                    return (Map<String, String>) f.get(null);
                })
                .ifSuccess(tables -> Language.loadFromJson(langStream, tables::put));
        } catch (IOException e) {
            fail(e);
        }
    }

    private static List<? extends DeferredRegister<?>> getDeferredRegisters(Class<?> declaredClass) {
        return Stream.of(declaredClass.getDeclaredFields())
            .filter(f -> f.getType() == DeferredRegister.class)
            .filter(f -> (f.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
            .filter(f -> (f.getModifiers() & Modifier.FINAL) == Modifier.FINAL)
            .peek(f -> f.setAccessible(true))
            .map(f -> {
                try {
                    return (DeferredRegister<?>) f.get(null);
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
            }).toList();
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void activateRegistry() {
        try {
            Method createRegistryMethod = DeferredRegister.class.getDeclaredMethod("addRegistry", NewRegistryEvent.class);
            Method fillMethod = NewRegistryEvent.class.getDeclaredMethod("fill");
            Constructor<NewRegistryEvent> newRegistryEventConstructor = NewRegistryEvent.class.getDeclaredConstructor();
            createRegistryMethod.setAccessible(true);
            fillMethod.setAccessible(true);
            newRegistryEventConstructor.setAccessible(true);

            NewRegistryEvent event = newRegistryEventConstructor.newInstance();
            List<? extends DeferredRegister<?>> registries = getDeferredRegisters(NeoForgeMod.class);
            for (DeferredRegister<?> registry : registries) {
                createRegistryMethod.invoke(registry, event);
            }
            // Register NeoForge registries
            Method neoForgeRegister = NeoForgeRegistriesSetup.class.getDeclaredMethod("registerRegistries", NewRegistryEvent.class);
            neoForgeRegister.setAccessible(true);
            neoForgeRegister.invoke(null, event);

            fillMethod.invoke(event);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static Consumer<RegisterEvent> registerForgeObjects() {
        List<? extends DeferredRegister<?>> registriesForgeMod = getDeferredRegisters(NeoForgeMod.class);
        return getRegisterer(registriesForgeMod);
    }

    public static Consumer<RegisterEvent> getRegisterer(List<? extends DeferredRegister<?>> registers) {
        try {
            Method handleEventMethod = DeferredRegister.class.getDeclaredMethod("addEntries", RegisterEvent.class);
            handleEventMethod.setAccessible(true);
            return event -> {
                try {
                    for (DeferredRegister<?> registry : registers) {
                        handleEventMethod.invoke(registry, event);
                    }
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
            };
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static void registerModObjects(Consumer<RegisterEvent> registerFunction) {
        try {
            var registries = BuiltInRegistries.REGISTRY;
            Constructor<RegisterEvent> registerEventConstructor = RegisterEvent.class.getDeclaredConstructor(ResourceKey.class, Registry.class);
            registerEventConstructor.setAccessible(true);

            for (var r : registries) {
                RegisterEvent event = registerEventConstructor.newInstance(
                    r.key(),
                    r
                );
                registerFunction.accept(event);
            }
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static Consumer<RegisterCapabilitiesEvent> registerNeoForgeCapabilities() {
        return CapabilityHooks::registerVanillaProviders;
    }

    private static void registerCapabilities(Consumer<RegisterCapabilitiesEvent> registerFunction) {
        try {
            var constructor = RegisterCapabilitiesEvent.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            var event = constructor.newInstance();
            registerFunction.accept(event);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static class DummyModContainer extends ModContainer {
        private final String name;

        public DummyModContainer(String name) {
            super(new DummyModInfo(name));
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " Test";
        }

        @Override
        public @Nullable IEventBus getEventBus() {
            return null;
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static final class DummyModInfo implements IModInfo, IConfigurable {
        private final String name;

        private DummyModInfo(String name) {
            this.name = name;
        }

        @Override
        public IModFileInfo getOwningFile() {
            return null;
        }

        @Override
        public IModLanguageLoader getLoader() {
            return null;
        }

        @Override
        public String getModId() {
            return name;
        }

        @Override
        public String getDisplayName() {
            return getModId() + " Test";
        }

        @Override
        public String getDescription() {
            return getDisplayName();
        }

        @Override
        public ArtifactVersion getVersion() {
            return new DefaultArtifactVersion("1.0");
        }

        @Override
        public List<? extends ModVersion> getDependencies() {
            return List.of();
        }

        @Override
        public List<? extends ForgeFeature.Bound> getForgeFeatures() {
            return List.of();
        }

        @Override
        public String getNamespace() {
            return getModId();
        }

        @Override
        public Map<String, Object> getModProperties() {
            return Map.of();
        }

        @Override
        public Optional<URL> getUpdateURL() {
            return Optional.empty();
        }

        @Override
        public Optional<URL> getModURL() {
            return Optional.empty();
        }

        @Override
        public Optional<String> getLogoFile() {
            return Optional.empty();
        }

        @Override
        public boolean getLogoBlur() {
            return false;
        }

        @Override
        public IConfigurable getConfig() {
            return this;
        }

        @Override
        public <T> Optional<T> getConfigElement(String... key) {
            return Optional.empty();
        }

        @Override
        public List<? extends IConfigurable> getConfigList(String... key) {
            return List.of();
        }

        @Override
        public String toString() {
            return "DummyModInfo[" +
                "name=" + name + ']';
        }
    }
}
