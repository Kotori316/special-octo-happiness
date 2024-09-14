package com.kotori316.debug.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandSetY {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command = Commands.literal("setY")
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.argument("y", IntegerArgumentType.integer())
                .executes(s -> moveEntity(s.getSource(), IntegerArgumentType.getInteger(s, "y"))));
        var setY = dispatcher.register(command);
        dispatcher.register(Commands.literal("y").requires(s -> s.hasPermission(Commands.LEVEL_GAMEMASTERS)).redirect(setY));
    }

    private static int moveEntity(CommandSourceStack stack, int y) throws CommandSyntaxException {
        var player = stack.getPlayerOrException();
        player.teleportTo(player.getX(), y, player.getZ());
        stack.sendSuccess(() -> Component.literal("teleport at y=" + y), true);
        return Command.SINGLE_SUCCESS;
    }
}
