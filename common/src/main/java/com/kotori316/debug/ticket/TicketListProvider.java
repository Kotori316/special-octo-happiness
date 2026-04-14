package com.kotori316.debug.ticket;

import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;

import java.util.List;

public interface TicketListProvider {
    TicketStorage test_utility_getTicket();

    static TicketStorage getTicket(DistanceManager manager) {
        return ((TicketListProvider) manager).test_utility_getTicket();
    }

    static List<Ticket> getTicketForPos(DistanceManager manager, ChunkPos pos) {
        return getTicket(manager).getTickets(pos.pack());
    }
}
