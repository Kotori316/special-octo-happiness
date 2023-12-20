package com.kotori316.testutil.mixin;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureTemplateManager.class)
public abstract class MixinStructureTemplateManager {
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;IS_RUNNING_IN_IDE:Z", opcode = Opcodes.GETSTATIC))
    private boolean injectTestStructures() {
        return true;
    }
}
