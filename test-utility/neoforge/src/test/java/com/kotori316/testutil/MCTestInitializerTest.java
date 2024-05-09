package com.kotori316.testutil;

import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MCTestInitializer.class)
class MCTestInitializerTest {
    @Test
    void accessBlock() {
        assertNotNull(assertDoesNotThrow(Blocks.AIR::defaultBlockState));
    }

    @Test
    void accessItem() {
        assertNotNull(assertDoesNotThrow(Items.AIR::asItem));
    }

    @Test
    void accessCapability() {
        assertNotNull(Capabilities.EnergyStorage.BLOCK);
    }

    @Nested
    class NeoForgeTest {
        @Test
        void accessIngredientType() {
            assertNotNull(assertDoesNotThrow(NeoForgeMod.COMPOUND_INGREDIENT_TYPE::get));
        }

        @Test
        void serializeCustomIngredient() {
            assertNotNull(assertDoesNotThrow(() -> Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, CompoundIngredient.of(Ingredient.of(Items.APPLE), Ingredient.of(Items.NAME_TAG)))));
        }

        @Test
        void accessDist() {
            assertNotNull(assertDoesNotThrow(NeoForgeTest::getDist));
        }

        private static Dist getDist() {
            return FMLEnvironment.dist;
        }
    }
}
