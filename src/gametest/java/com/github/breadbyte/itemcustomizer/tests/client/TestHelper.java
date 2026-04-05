package com.github.breadbyte.itemcustomizer.tests.client;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.util.InputUtil;

@SuppressWarnings("UnstableApiUsage")
public class TestHelper {
    public static void SendMessage(ClientGameTestContext context, String message) {
        context.getInput().pressKey(InputUtil.GLFW_KEY_T);
        context.getInput().typeChars(message);
        context.getInput().pressKey(InputUtil.GLFW_KEY_ENTER);
    }

    public static void SendCommand(ClientGameTestContext context, String command) {
        SendMessage(context, "/" + command);
    }
}
