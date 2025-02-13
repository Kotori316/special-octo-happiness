package com.kotori316.testutil;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AccessTest {
    @Test
    void accessDirection() {
        var direction = Direction.NORTH;
        assertNotNull(direction);
    }

    @Test
    void accessClass() {
        var clazz = Direction.class;
        assertTrue(clazz.isEnum());
    }

    @Test
    void accessLength() {
        var values = Direction.class.getEnumConstants();
        assertNotNull(values);
        assertTrue(values.length > 0);
    }
}
