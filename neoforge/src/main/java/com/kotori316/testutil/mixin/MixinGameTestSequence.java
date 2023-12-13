package com.kotori316.testutil.mixin;

import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameTestSequence.class)
public final class MixinGameTestSequence {
    @Final
    @Shadow
    GameTestInfo parent;

    @Shadow
    private void tick(long pTicks) {
    }

    @Inject(method = "executeWithoutFail", at = @At("HEAD"), cancellable = true)
    private void executeWithoutFailCatchAssertion(Runnable pTask, CallbackInfo ci) {
        try {
            pTask.run();
        } catch (AssertionError | GameTestAssertException e) {
            this.parent.fail(e);
        }
        ci.cancel();
    }

    @Inject(method = "tickAndFailIfNotComplete", at = @At("HEAD"), cancellable = true)
    private void tickAndFailIfNotCompleteCatchAssertion(long pTicks, CallbackInfo ci) {
        try {
            this.tick(pTicks);
        } catch (AssertionError | GameTestAssertException e) {
            this.parent.fail(e);
        }
        ci.cancel();
    }
}
