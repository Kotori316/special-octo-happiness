package com.kotori316.debug.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.io.Serial;

public class CommandCrash {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command =
            Commands.literal("crash")
                .requires(source -> source.hasPermission(2))
                .executes(c -> {
                    throw new CrashException();
                });
        dispatcher.register(command);
    }

    private static class CrashException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = -5807864695978875688L;

        public CrashException() {
            super("Manually caused exception.");
        }
    }
}
