package com.kotori316.testutil.mixin;

import com.kotori316.testutil.common.TestUtilityCommon;
import com.kotori316.testutil.common.reporter.ReporterRegister;
import net.fabricmc.fabric.impl.gametest.FabricGameTestHelper;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(FabricGameTestHelper.class)
public class FabricGameTestHelperMixin {
    @Inject(method = "runHeadlessServer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V", shift = At.Shift.BEFORE, remap = false))
    private static void changeGlobalTestReporter(LevelStorageSource.LevelStorageAccess session, PackRepository resourcePackManager, CallbackInfo ci) {
        ReporterRegister.changeReporter();
        TestUtilityCommon.GENERAL.info("Inject CreateFileReporter by {}", TestUtilityCommon.MOD_ID);
    }
}
