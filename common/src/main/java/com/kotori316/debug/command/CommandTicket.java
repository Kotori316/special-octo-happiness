package com.kotori316.debug.command;

import com.kotori316.debug.ticket.TicketListProvider;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.ChunkPos;

import java.util.Collection;

public final class CommandTicket {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var checkTicketCommand = Commands.literal("checkTicket")
            .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))
            .executes(c -> checkTicket(c, BlockPos.containing(c.getSource().getPosition())))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(c -> checkTicket(c, BlockPosArgument.getBlockPos(c, "pos")))
            );
        dispatcher.register(checkTicketCommand);
    }

    private static int checkTicket(CommandContext<CommandSourceStack> commandContext, BlockPos pos) {
        var data = commandContext.getSource();
        var distanceManager = data.getLevel().getChunkSource().chunkMap.getDistanceManager();
        var chunkPos = new ChunkPos(pos);
        var tickets = TicketListProvider.getTicketForPos(distanceManager, chunkPos);
        var message = Component.literal("Tickets for chunk %s(%s) is %s".formatted(chunkPos, pos.toShortString(), tickets.size()))
            .append(Component.literal(System.lineSeparator()))
            .append(formatTickets(tickets));
        data.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    static Component formatTickets(Collection<Ticket> tickets) {
        return tickets.stream()
            .map(Ticket::toString)
            .map(Component::literal)
            .reduce(Component.empty(), (a, b) -> a.append(b).append(Component.literal(System.lineSeparator())));
    }
}
