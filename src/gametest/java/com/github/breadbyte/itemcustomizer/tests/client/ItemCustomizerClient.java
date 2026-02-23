package com.github.breadbyte.itemcustomizer.tests.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

@SuppressWarnings("UnstableApiUsage")
public class ItemCustomizerClient implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext ctx) {
        try (TestSingleplayerContext singleplayer = ctx.worldBuilder().create()) {
            singleplayer.getClientWorld().waitForChunksRender();
            ctx.takeScreenshot("example-mod-singleplayer-test");
            ctx.getInput().typeChars("test");
            ctx.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
