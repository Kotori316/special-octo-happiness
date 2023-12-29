package com.kotori316.debug.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;

public class CommandToggleMode {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command =
            Commands.literal("toggle")
                .requires(source -> source.hasPermission(2))
                .executes(c -> {
                    var player = c.getSource().getPlayerOrException();
                    if (player.isCreative()) {
                        player.setGameMode(GameType.SURVIVAL);
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 5));
                        player.displayClientMessage(Component.literal("Changed to Survival"), false);
                    } else {
                        player.setGameMode(GameType.CREATIVE);
                        player.displayClientMessage(Component.literal("Changed to Creative"), false);
                    }
                    return Command.SINGLE_SUCCESS;
                });
        dispatcher.register(command);
    }
}
