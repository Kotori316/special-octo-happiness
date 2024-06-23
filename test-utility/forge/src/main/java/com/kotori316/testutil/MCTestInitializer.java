package com.kotori316.testutil;

import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.ForgeFeature;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.*;
import net.minecraftforge.server.LanguageHook;
import net.minecraftforge.unsafe.UnsafeHacks;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.support.ReflectionSupport;
import sun.misc.Unsafe;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public final class MCTestInitializer implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) {
        setUp(Objects.requireNonNullElse(System.getenv("target_mod"), "none"), () -> {
        });
    }

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    public static void setUp(String modId, Runnable additional) {
        setUp(modId, additional, e -> {
        });
    }

    public static synchronized void setUp(String modId, Runnable additional, Consumer<RegisterEvent> modResourceRegister) {
        if (!INITIALIZED.getAndSet(true)) {
            resolveInfoCmpError();
            changeDist();
            SharedConstants.tryDetectVersion();
            setHandler();
            Bootstrap.bootStrap();
            unfreezeGameData();
            ModLoadingContext.get().setActiveContainer(new DummyModContainer(modId));
            mockCapability();
            mockRegistries();
            setFluidType();
            setLanguage(modId);
            activateRegistry();
            registerModObjects(modResourceRegister.andThen(registerForgeObjects()));
            additional.run();
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
                Class.forName("net.minecraftforge.fml.loading.targets.ForgeUserdevLaunchHandler$Data")
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

    @SuppressWarnings("unchecked")
    private static void mockCapability() {
        try {
            var method = CapabilityManager.class.getDeclaredMethod("get", String.class, ResourceLocation.class, boolean.class);
            method.setAccessible(true);
            var cap_IEnergyStorage = (Capability<IEnergyStorage>) method.invoke(null, "IEnergyStorage", null, false);
            var cap_IFluidHandler = (Capability<IFluidHandler>) method.invoke(null, "IFluidHandler", null, false);
            var cap_IFluidHandlerItem = (Capability<IFluidHandlerItem>) method.invoke(null, "IFluidHandlerItem", null, false);
            var cap_IItemHandler = (Capability<IItemHandler>) method.invoke(null, "IItemHandler", null, false);
            try (var mocked = mockStatic(CapabilityManager.class)) {
                mocked.when(() -> CapabilityManager.get(any()))
                    .thenReturn(cap_IEnergyStorage)
                    .thenReturn(cap_IFluidHandler)
                    .thenReturn(cap_IFluidHandlerItem)
                    .thenReturn(cap_IItemHandler);
                assertEquals(cap_IEnergyStorage, ForgeCapabilities.ENERGY);
                assertEquals(cap_IFluidHandler, ForgeCapabilities.FLUID_HANDLER);
                assertEquals(cap_IFluidHandlerItem, ForgeCapabilities.FLUID_HANDLER_ITEM);
                assertEquals(cap_IItemHandler, ForgeCapabilities.ITEM_HANDLER);
            }
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
        assertNotNull(ForgeCapabilities.ENERGY);
        assertNotNull(ForgeCapabilities.FLUID_HANDLER);
        assertNotNull(ForgeCapabilities.FLUID_HANDLER_ITEM);
        assertNotNull(ForgeCapabilities.ITEM_HANDLER);
    }

    private static void mockRegistries() {
        try {
            mockRegistry(ForgeRegistries.ITEMS, ForgeRegistries.class.getDeclaredField("ITEMS"));
            mockRegistry(ForgeRegistries.BLOCKS, ForgeRegistries.class.getDeclaredField("BLOCKS"));
            mockRegistry(ForgeRegistries.FLUIDS, ForgeRegistries.class.getDeclaredField("FLUIDS"));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings({"unchecked", "deprecation", "UnstableApiUsage"})
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
    }

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
            UnsafeHacks.setField(field, Fluids.EMPTY, airType);
            UnsafeHacks.setField(field, Fluids.WATER, waterType);
            UnsafeHacks.setField(field, Fluids.LAVA, lavaType);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static void setLanguage(String modId) {
        LanguageHook.loadForgeAndMCLangs();
        Try.call(() -> LanguageHook.class.getDeclaredMethod("loadLocaleData", InputStream.class))
            .andThenTry(m -> ReflectionSupport.invokeMethod(m, null, MCTestInitializer.class.getResourceAsStream("/assets/%s/lang/en_us.json".formatted(modId))));
    }

    @SuppressWarnings("unchecked")
    private static void activateRegistry() {
        try {
            Field forgeRegistriesField = ForgeRegistries.class.getDeclaredField("registries");
            Class<?> eventDispatcherClass = Class.forName("net.minecraftforge.registries.DeferredRegister$EventDispatcher");
            Constructor<?> eventDispatcherConstructor = eventDispatcherClass.getDeclaredConstructor(DeferredRegister.class);
            Method createRegistryMethod = eventDispatcherClass.getDeclaredMethod("createRegistry", NewRegistryEvent.class);
            Method fillMethod = NewRegistryEvent.class.getDeclaredMethod("fill");
            forgeRegistriesField.setAccessible(true);
            eventDispatcherConstructor.setAccessible(true);
            createRegistryMethod.setAccessible(true);
            fillMethod.setAccessible(true);

            NewRegistryEvent event = new NewRegistryEvent();
            List<DeferredRegister<?>> registries = (List<DeferredRegister<?>>) forgeRegistriesField.get(null);
            for (DeferredRegister<?> registry : registries) {
                var eventDispatcher = eventDispatcherConstructor.newInstance(registry);
                createRegistryMethod.invoke(eventDispatcher, event);
            }
            fillMethod.invoke(event);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    private static Consumer<RegisterEvent> registerForgeObjects() {
        try {
            Field forgeModRegistriesField = ForgeMod.class.getDeclaredField("registries");
            forgeModRegistriesField.setAccessible(true);

            List<DeferredRegister<?>> registriesForgeMod = (List<DeferredRegister<?>>) forgeModRegistriesField.get(null);
            return getRegisterer(registriesForgeMod);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public static Consumer<RegisterEvent> getRegisterer(List<DeferredRegister<?>> registers) {
        try {
            Class<?> eventDispatcherClass = Class.forName("net.minecraftforge.registries.DeferredRegister$EventDispatcher");
            Constructor<?> eventDispatcherConstructor = eventDispatcherClass.getDeclaredConstructor(DeferredRegister.class);
            Method handleEventMethod = eventDispatcherClass.getDeclaredMethod("handleEvent", RegisterEvent.class);
            eventDispatcherConstructor.setAccessible(true);
            handleEventMethod.setAccessible(true);
            return event -> {
                try {
                    for (DeferredRegister<?> registry : registers) {
                        var eventDispatcher = eventDispatcherConstructor.newInstance(registry);
                        handleEventMethod.invoke(eventDispatcher, event);
                    }
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
            };
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void registerModObjects(Consumer<RegisterEvent> registerFunction) {
        var registries = RegistryManager.ACTIVE.getRegistries().values();
        try {
            Constructor<RegisterEvent> registerEventConstructor = RegisterEvent.class.getDeclaredConstructor(ResourceKey.class, ForgeRegistry.class, Registry.class);
            registerEventConstructor.setAccessible(true);

            for (var r : registries) {
                RegisterEvent event = registerEventConstructor.newInstance(
                    r.getRegistryKey(),
                    RegistryManager.ACTIVE.getRegistry(r.getRegistryKey()),
                    null
                );
                registerFunction.accept(event);
            }
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static class DummyModContainer extends ModContainer {
        private final String name;

        public DummyModContainer(String name) {
            super(new DummyModInfo(name));
            this.name = name;
            contextExtension = Object::new;
        }

        @Override
        public boolean matches(Object mod) {
            return mod == getMod();
        }

        @Override
        public Object getMod() {
            return name + " Test";
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

        @SuppressWarnings("unchecked")
        @Override
        public <T> Optional<T> getConfigElement(String... key) {
            if (key.length > 0 && "displayTest".equals(key[0])) {
                return (Optional<T>) Optional.of("MATCH_VERSION");
            }
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
