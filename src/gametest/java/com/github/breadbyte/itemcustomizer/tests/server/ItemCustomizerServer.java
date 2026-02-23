package com.github.breadbyte.itemcustomizer.tests.server;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.item.Items;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;

import java.lang.reflect.Method;

public class ItemCustomizerServer implements CustomTestMethodInvoker {

    @GameTest
    public void CreateTestPlayer(TestContext context) {
        var player = context.createMockPlayer(GameMode.DEFAULT);
        player.setStackInHand(Hand.MAIN_HAND, Items.DIAMOND_SWORD.getDefaultStack());

        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.DIAMOND_SWORD, "Player should have a diamond sword in hand");
    }

    @Override
    public void invokeTestMethod(TestContext testContext, Method method) throws ReflectiveOperationException {
        method.invoke(this, testContext);
    }
}
