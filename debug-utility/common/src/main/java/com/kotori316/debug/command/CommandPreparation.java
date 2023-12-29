package com.kotori316.debug.command;

import com.kotori316.debug.ServerSetting;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class CommandPreparation {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command =
            Commands.literal("preparation")
                .requires(source -> source.hasPermission(2))
                .executes(c -> {
                    if (c.getSource().getEntity() instanceof ServerPlayer player) {
                        var level = c.getSource().getLevel();
                        ServerSetting.onLogin(player, level);
                    }
                    return Command.SINGLE_SUCCESS;
                });
        dispatcher.register(command);
    }
}
