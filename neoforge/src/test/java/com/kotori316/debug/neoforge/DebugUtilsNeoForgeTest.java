package com.kotori316.debug.neoforge;

import net.neoforged.bus.api.IEventBus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class DebugUtilsNeoForgeTest {
    @Test
    void checkConstructor() {
        var clazz = DebugUtilsNeoForge.class;
        var constructors = clazz.getConstructors();
        assertEquals(1, constructors.length);
        var first = constructors[0];
        Class<?>[] expected = {IEventBus.class};
        assertArrayEquals(expected, first.getParameterTypes());
    }
}
