package com.kotori316.test_utility;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtility implements ModInitializer {
    public static final String modId = "test_utility";
    public static final Logger LOGGER = LoggerFactory.getLogger(modId);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");
    }
}