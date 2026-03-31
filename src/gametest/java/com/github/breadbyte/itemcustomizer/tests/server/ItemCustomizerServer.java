package com.github.breadbyte.itemcustomizer.tests.server;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.ModelApplyParams;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply.ModelApplyOperations;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.lang.reflect.Method;

public class ItemCustomizerServer implements CustomTestMethodInvoker {
    @Override
    public void invokeTestMethod(TestContext testContext, Method method) throws ReflectiveOperationException {
        var player = testContext.createMockPlayer(GameMode.CREATIVE);
        player.setStackInHand(Hand.MAIN_HAND, Items.DIAMOND_SWORD.getDefaultStack());
        player.addCommandTag("InGameTest");
        testContext.getWorld().setMobSpawnOptions(false);
        testContext.getWorld().getServer().getPlayerManager().addToOperators(player.getPlayerConfigEntry());

        method.invoke(this, testContext, player);
    }

    @GameTest
    public void TestApplyModel(TestContext context, PlayerEntity player) {
        var playerHand = player.getMainHandStack();
        var targetModel = new ModelPath("minecraft", "", "stone");
        var cmd = new CustomModelDefinition(targetModel, "");
        var modelApply = new ModelApplyOperations();
        var params = new ModelApplyParams(playerHand, targetModel, cmd);
        modelApply.apply(params);

        context.assertEquals(
                Identifier.of("minecraft", "stone"),
                playerHand.getComponents().get(DataComponentTypes.ITEM_MODEL),
                "item model");
    }
}
