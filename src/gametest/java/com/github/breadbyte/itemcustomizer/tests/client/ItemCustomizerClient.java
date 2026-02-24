package com.github.breadbyte.itemcustomizer.tests.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.rule.GameRules;

@SuppressWarnings("UnstableApiUsage")
public class ItemCustomizerClient implements FabricClientGameTest {

    final ItemStack DEFAULT_ITEM = Items.DIAMOND_SWORD.getDefaultStack();

    @Override
    public void runTest(ClientGameTestContext ctx) {
        ctx.runOnClient(client -> {
            client.options.setServerViewDistance(2);
            client.options.setPerspective(Perspective.FIRST_PERSON);
        });

        try (TestSingleplayerContext singleplayer = ctx.worldBuilder()
                .adjustSettings((c) -> {
                    c.setDifficulty(Difficulty.PEACEFUL);
                    c.setBonusChestEnabled(false);
                }).create()) {

            TestServerContext serverContext = singleplayer.getServer();
            setup(serverContext, singleplayer);
            singleplayer.getClientWorld().waitForChunksRender();
        }
    }

    public void setup(TestServerContext serverContext, TestSingleplayerContext singleplayerContext) {
    }
}
