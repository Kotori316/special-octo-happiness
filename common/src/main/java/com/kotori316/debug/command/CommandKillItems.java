package com.kotori316.debug.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;

public class CommandKillItems {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command =
            Commands.literal("killItems")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(c -> {
                    var world = c.getSource().getLevel();
                    var entities = world.getEntities(EntityType.ITEM, EntitySelector.ENTITY_STILL_ALIVE);
                    entities.forEach(e -> e.kill(world));
                    c.getSource().sendSuccess(() -> Component.translatable("commands.kill.success.multiple", entities.size()), true);
                    return Command.SINGLE_SUCCESS;
                });
        dispatcher.register(command);
    }
}
