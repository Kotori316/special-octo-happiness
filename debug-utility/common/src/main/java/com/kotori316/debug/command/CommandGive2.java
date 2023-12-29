package com.kotori316.debug.command;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.server.level.ServerPlayer;

public class CommandGive2 {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        var giveToPlayer =
            Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("item", ItemArgument.item(context))
                    .then(Commands.argument("count", IntegerArgumentType.integer(1))
                        .executes(c -> {
                            var item = ItemArgument.getItem(c, "item");
                            var count = IntegerArgumentType.getInteger(c, "count");
                            var targets = EntityArgument.getPlayers(c, "targets");
                            give(targets, item, count);
                            return Command.SINGLE_SUCCESS;
                        })));
        var command =
            Commands.literal("give2")
                .requires(source -> source.hasPermission(2))
                .then(giveToPlayer);
        dispatcher.register(command);
    }

    private static void give(Collection<ServerPlayer> players, ItemInput item, int count) throws CommandSyntaxException {
        for (ServerPlayer player : players) {
            var stack = item.createItemStack(count, false);
            var slot = player.getInventory().getFreeSlot();
            if (slot >= 0)
                player.getInventory().items.set(slot, stack);
        }
    }
}
