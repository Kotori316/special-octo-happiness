package com.kotori316.testutil.common;

import net.minecraft.gametest.framework.GameTestException;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;

public final class TestFunctionException extends GameTestException {
    @Serial
    private static final long serialVersionUID = -1152265008171601373L;
    private final Component description;

    public TestFunctionException(@NotNull String testName, @Nullable String message) {
        super(message != null ? message : "[no message]");
        this.description = Component.literal("Test %s failed by %s".formatted(testName, super.getMessage()));
    }

    @Override
    public Component getDescription() {
        return this.description;
    }
}
