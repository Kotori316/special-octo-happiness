package com.kotori316.testutil;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void accessValue() {
        var clazz = Direction.class;
        System.out.println(Arrays.toString(clazz.getDeclaredMethods()));
        var method = assertDoesNotThrow(() -> clazz.getDeclaredMethod("values"));
        assertNotNull(method);
        var array = assertDoesNotThrow(() -> method.invoke(null));
        assertInstanceOf(Direction[].class, array);
    }
}
