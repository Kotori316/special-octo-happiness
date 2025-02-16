package com.kotori316.testutil.mixin;

import com.kotori316.debug.ticket.TicketListProvider;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.Ticket;
import net.minecraft.util.SortedArraySet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DistanceManager.class)
public abstract class MixinDistanceManager implements TicketListProvider {
    @Shadow
    @Final
    Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets;

    @Override
    public Long2ObjectFunction<SortedArraySet<Ticket<?>>> test_utility_getTicket() {
        return this.tickets;
    }
}
