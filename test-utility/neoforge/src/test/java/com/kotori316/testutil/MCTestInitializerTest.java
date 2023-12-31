package com.kotori316.testutil;

import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
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
            assertNotNull(assertDoesNotThrow(NeoForgeMod.VANILLA_INGREDIENT_TYPE::get));
        }

        @Test
        void serializeCustomIngredient() {
            assertNotNull(assertDoesNotThrow(() -> Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, NBTIngredient.of(false, new ItemStack(Items.APPLE)))));
        }
    }
}
