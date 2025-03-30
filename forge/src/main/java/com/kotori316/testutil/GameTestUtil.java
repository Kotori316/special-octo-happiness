package com.kotori316.testutil;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.support.ReflectionSupport;

@SuppressWarnings("unused") // All methods are used in other projects.
public final class GameTestUtil {
    public static BlockPos getBasePos(GameTestHelper helper) {
        return Try.call(() -> GameTestHelper.class.getDeclaredField("testInfo"))
            .andThen(f -> ReflectionSupport.tryToReadFieldValue(f, helper))
            .andThenTry(GameTestInfo.class::cast)
            .andThenTry(GameTestInfo::getTestInstanceBlockEntity)
            .andThenTry(TestInstanceBlockEntity::getStartCorner)
            .getOrThrow(RuntimeException::new);
    }

    public static ICondition.IContext getContext(GameTestHelper helper) {
        return Try.success(helper.getLevel())
            .andThenTry(ServerLevel::getServer)
            .andThenTry(MinecraftServer::getServerResources)
            .andThenTry(MinecraftServer.ReloadableResources::managers)
            .andThenTry(ReloadableServerResources::getConditionContext)
            .getOrThrow(RuntimeException::new);
    }

}
