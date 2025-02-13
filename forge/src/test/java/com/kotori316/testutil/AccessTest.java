package com.kotori316.testutil;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class AccessTest {
    @Test
    void accessDirection() {
        var direction = Direction.NORTH;
        assertNotNull(direction);
    }

    @Test
    void accessValues() {
        var values = assertDoesNotThrow(Direction::values);
        assertNotNull(values);
        assertNotEquals(0, values.length);
    }
}
