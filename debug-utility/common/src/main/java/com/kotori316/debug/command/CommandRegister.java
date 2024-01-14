package com.kotori316.debug.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public class CommandRegister {
    public static void registerAll(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        CommandCrash.register(dispatcher);
        CommandGive2.register(dispatcher, context);
        CommandKillItems.register(dispatcher);
        CommandPreparation.register(dispatcher);
        CommandSetStone.register(dispatcher);
        CommandToggleMode.register(dispatcher);
        CommandSetY.register(dispatcher);
    }
}
