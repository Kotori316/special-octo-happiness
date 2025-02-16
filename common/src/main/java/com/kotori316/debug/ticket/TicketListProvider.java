package com.kotori316.debug.ticket;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.Ticket;
import net.minecraft.util.SortedArraySet;

public interface TicketListProvider {
    Long2ObjectFunction<SortedArraySet<Ticket<?>>> test_utility_getTicket();

    static Long2ObjectFunction<SortedArraySet<Ticket<?>>> getTicket(DistanceManager manager) {
        return ((TicketListProvider) manager).test_utility_getTicket();
    }
}
