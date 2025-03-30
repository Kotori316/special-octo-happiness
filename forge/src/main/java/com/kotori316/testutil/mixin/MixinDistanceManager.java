package com.kotori316.testutil.mixin;

import com.kotori316.debug.ticket.TicketListProvider;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DistanceManager.class)
public abstract class MixinDistanceManager implements TicketListProvider {
    @Shadow
    @Final
    TicketStorage ticketStorage;

    @Override
    public TicketStorage test_utility_getTicket() {
        return this.ticketStorage;
    }
}
