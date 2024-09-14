package com.kotori316.testutil;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MCTestInitializer.class)
class MCTestInitializerTest {
    @Test
    void access() {
        assertNotNull(assertDoesNotThrow(Blocks.AIR::defaultBlockState));
    }

    @Test
    void accessDist() {
        assertNotNull(assertDoesNotThrow(MCTestInitializerTest::getDist));
    }

    private static Dist getDist() {
        return FMLEnvironment.dist;
    }
}
