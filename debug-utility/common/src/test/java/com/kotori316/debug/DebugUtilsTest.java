package com.kotori316.debug;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ConstantValue")
final class DebugUtilsTest {
    @Test
    void checkModId() {
        assertTrue(DebugUtils.MOD_ID.contains("debug"));
    }
}
