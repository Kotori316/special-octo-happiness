package com.kotori316.debug.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("SpellCheckingInspection")
public class CommandSetStone {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command =
            Commands.literal("setstone")
                .requires(source -> source.hasPermission(2))
                .executes(c -> setStone(c, BlockPos.containing(c.getSource().getPosition()).below()))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(c -> setStone(c, BlockPosArgument.getLoadedBlockPos(c, "pos"))));
        dispatcher.register(command);
    }

    private static int setStone(CommandContext<CommandSourceStack> context, BlockPos pos) throws CommandSyntaxException {
        var level = context.getSource().getLevel();
        if (level.getBlockEntity(pos) instanceof Container container) {
            container.clearContent();
        }
        if (level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_CLIENTS)) {
            level.blockUpdated(pos, Blocks.STONE);
            context.getSource().sendSuccess(() -> Component.translatable("commands.setblock.success", pos.getX(), pos.getY(), pos.getZ()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.setblock.failed")).create();
        }
    }
}
