package com.kotori316.testutil.mixin;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.TestFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Consumer;

@Mixin(GameTestRegistry.class)
public final class MixinGameTestRegistry {

    @Redirect(
        method = "turnMethodIntoTestFunction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/gametest/framework/GameTestRegistry;turnMethodIntoConsumer(Ljava/lang/reflect/Method;)Ljava/util/function/Consumer;"
        )
    )
    private static Consumer<?> hookTurnMethodIntoConsumer(Method pTestMethod) {
        return helper -> {
            try {
                pTestMethod.trySetAccessible();
                final Object instance;
                if (Modifier.isStatic(pTestMethod.getModifiers())) {
                    instance = null;
                } else {
                    var constructor = pTestMethod.getDeclaringClass().getDeclaredConstructor();
                    constructor.trySetAccessible();
                    instance = constructor.newInstance();
                }
                if (pTestMethod.getParameterCount() == 0) {
                    pTestMethod.invoke(instance);
                    // Assuming this test finishes in a tick.
                    ((GameTestHelper) helper).succeed();
                } else {
                    // A sequence will stop if GameTestHelper#succeed is called.
                    // The actual content can't be known from here, so call GameTestHelper#succeed in the test.
                    pTestMethod.invoke(instance, helper);
                }
            } catch (ReflectiveOperationException | AssertionError e) {
                if (e.getCause() instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                } else {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Redirect(
        method = "register(Ljava/lang/reflect/Method;Ljava/util/Set;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/gametest/framework/GameTestRegistry;useTestGeneratorMethod(Ljava/lang/reflect/Method;)Ljava/util/Collection;"
        )
    )
    @SuppressWarnings("unchecked")
    private static Collection<TestFunction> hookUseTestGeneratorMethod(Method method) {
        try {
            method.trySetAccessible();
            final Object instance;
            if (Modifier.isStatic(method.getModifiers())) {
                instance = null;
            } else {
                var constructor = method.getDeclaringClass().getDeclaredConstructor();
                constructor.trySetAccessible();
                instance = constructor.newInstance();
            }
            return (Collection<TestFunction>) method.invoke(instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
