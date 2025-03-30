package com.kotori316.testutil.common;

import com.google.common.hash.HashCode;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.file.Path;

public class TestUtilityCommon {
    public static final String MOD_ID = "test_utility";
    public static final Logger GENERAL = LoggerFactory.getLogger("TestUtil/General");
    public static final Logger DATA_GENERATOR_LOGGER = LoggerFactory.getLogger("TestUtil/DataGen");
    public static final Logger TEST_LOADER_LOGGER = LoggerFactory.getLogger("TestUtil/TestLoad");

    public static void logTestName(Object maybeTest, @Nullable Method createFrom) {
        if (Boolean.parseBoolean(System.getenv("TEST_UTILITY_LOG_ALL_TEST"))) {
            TEST_LOADER_LOGGER.info("{} is loaded.", maybeTest);
        }
    }

    public static void logDataGeneration(Path path, HashCode hashCode) {
        if (Boolean.parseBoolean(System.getenv("TEST_UTILITY_LOG_ALL_DATA"))) {
            DATA_GENERATOR_LOGGER.info("Generating {} at {}", hashCode.toString(), path.toAbsolutePath().normalize());
        }
    }

    public static void throwExceptionAt(GameTestHelper helper, BlockPos relativePos, String message)
        throws GameTestAssertPosException {
        var absolutePos = helper.absolutePos(relativePos);
        throw new GameTestAssertPosException(Component.literal(message), absolutePos, relativePos, (int) helper.getTick());
    }
}
