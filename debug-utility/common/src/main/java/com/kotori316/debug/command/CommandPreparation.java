package com.kotori316.debug.command;

import com.kotori316.debug.ServerSetting;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class CommandPreparation {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command =
            Commands.literal("du-preparation")
                .requires(source -> source.hasPermission(2))
                .executes(c -> {
                    @Nullable ServerPlayer p = c.getSource().getEntity() instanceof ServerPlayer player ? player : null;
                    ServerSetting.onLogin(c.getSource().getServer(), p, true);
                    return Command.SINGLE_SUCCESS;
                });
        dispatcher.register(command);
    }
}
