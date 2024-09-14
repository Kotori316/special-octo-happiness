package com.kotori316.testutil;

import com.kotori316.testutil.common.TestUtilityCommon;
import net.fabricmc.api.ModInitializer;

public class TestUtility implements ModInitializer {
    @Override
    public void onInitialize() {
        TestUtilityCommon.GENERAL.info("Hello Fabric world!");
    }
}