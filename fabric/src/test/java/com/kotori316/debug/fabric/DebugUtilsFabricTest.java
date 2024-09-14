package com.kotori316.debug.fabric;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class DebugUtilsFabricTest {
    @Test
    void createInstance() {
        var clazz = DebugUtilsFabric.class;
        var instance = assertDoesNotThrow(() -> clazz.getConstructor().newInstance());
        assertNotNull(instance);
    }
}
