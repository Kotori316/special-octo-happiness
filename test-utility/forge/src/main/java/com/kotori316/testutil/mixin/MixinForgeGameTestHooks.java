package com.kotori316.testutil.mixin;

import com.kotori316.testutil.common.TestUtilityCommon;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ForgeGameTestHooks.class)
public final class MixinForgeGameTestHooks {
    @Inject(method = "addTest", at = @At(
        value = "INVOKE",
        target = "Ljava/util/Collection;add(Ljava/lang/Object;)Z",
        shift = At.Shift.AFTER,
        by = 1
    ))
    private static void logTestName(Collection<TestFunction> functions, Set<String> classes, Set<String> filters, TestFunction func, CallbackInfo ci) {
        TestUtilityCommon.logTestName(func, null);
    }
}
