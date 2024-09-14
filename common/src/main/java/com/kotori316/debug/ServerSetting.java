package com.kotori316.debug;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ServerSetting {
    public static void onLogin(MinecraftServer server, @Nullable Player player, boolean force) {
        var gameRule = server.getGameRules();
        if (!force && !gameRule.getBoolean(GameRules.RULE_DAYLIGHT)) return;

        gameRule.getRule(GameRules.RULE_DAYLIGHT).set(false, server);
        gameRule.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
        gameRule.getRule(GameRules.RULE_KEEPINVENTORY).set(true, server);
        gameRule.getRule(GameRules.RULE_FIRE_DAMAGE).set(false, server);
        gameRule.getRule(GameRules.RULE_FALL_DAMAGE).set(false, server);
        gameRule.getRule(GameRules.RULE_DROWNING_DAMAGE).set(false, server);
        gameRule.getRule(GameRules.RULE_DO_WARDEN_SPAWNING).set(false, server);

        server.getAllLevels().forEach(s -> {
            s.setWeatherParameters(0, 0, false, false);
            s.setDayTime(6000L);
        });

        if (player != null) {
            Stream.of(
                    "Set game rules for debugging.",
                    "Seed = %s".formatted(server.overworld().getSeed())
                ).map(Component::literal)
                .forEach(m -> player.displayClientMessage(m, false));
            var potionStack = new ItemStack(Items.SPLASH_POTION);
            potionStack.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.LONG_NIGHT_VISION).withEffectAdded(new MobEffectInstance(MobEffects.WATER_BREATHING, 20 * 60 * 8))
            );
            if (!player.getInventory().contains(potionStack)) {
                player.addItem(potionStack);
            }
        }
        DebugUtils.LOGGER.info("Set ServerSetting from {}, force={}", DebugUtils.MOD_ID, force);
    }


    public static void onLogin(Player player, Level level, boolean force) {
        var server = level.getServer();
        if (server == null) return;
        onLogin(server, player, force);
    }
}
