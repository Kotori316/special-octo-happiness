package com.kotori316.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Stream;

public class ServerSetting {
    public static void onLogin(Player player, Level level) {
        var server = level.getServer();
        var gameRule = level.getGameRules();
        if (!gameRule.getBoolean(GameRules.RULE_DAYLIGHT)) return;

        gameRule.getRule(GameRules.RULE_DAYLIGHT).set(false, server);
        gameRule.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
        gameRule.getRule(GameRules.RULE_KEEPINVENTORY).set(true, server);
        gameRule.getRule(GameRules.RULE_FIRE_DAMAGE).set(false, server);
        gameRule.getRule(GameRules.RULE_FALL_DAMAGE).set(false, server);
        gameRule.getRule(GameRules.RULE_DROWNING_DAMAGE).set(false, server);
        gameRule.getRule(GameRules.RULE_DO_WARDEN_SPAWNING).set(false, server);
        level.getLevelData().setRaining(false);

        if (!level.isClientSide && server != null) {
            server.getAllLevels().forEach(s -> {
                s.setWeatherParameters(0, 0, false, false);
                s.setDayTime(6000L);
            });
            Stream.of(
                    "Set game rules for debugging.",
                    "Seed = %s".formatted(((ServerLevel) level).getSeed())
                ).map(Component::literal)
                .forEach(m -> player.displayClientMessage(m, false));
        }

        var potionStack = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.LONG_NIGHT_VISION);
        PotionUtils.setCustomEffects(potionStack, List.of(new MobEffectInstance(MobEffects.WATER_BREATHING, 20 * 60 * 8)));
        if (!player.getInventory().contains(potionStack)) {
            player.addItem(potionStack);
        }
    }
}
