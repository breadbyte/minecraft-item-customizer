package com.github.breadbyte.itemcustomizer.tests.server;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

public class ItemCustomizerServer implements CustomTestMethodInvoker {
    @Override
    public void invokeTestMethod(TestContext testContext, Method method) throws ReflectiveOperationException {
        method.invoke(this, testContext);
    }

    @GameTest
    public void TestApplyModel(TestContext context) throws CommandSyntaxException {
        var player = preinit(context);
        executeCommand(context, player, "model apply minecraft stone");
        context.assertEquals(
                Identifier.of("minecraft", "stone"),
                player.getMainHandStack().getComponents().get(DataComponentTypes.ITEM_MODEL),
                Text.empty());
    }
    private PlayerEntity preinit(TestContext context) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "TestPlayer");
        ServerPlayerEntity s = new ServerPlayerEntity(context.getWorld().getServer(), context.getWorld(), profile, SyncedClientOptions.createDefault());
        s.setStackInHand(Hand.MAIN_HAND, Items.DIAMOND_SWORD.getDefaultStack());

        return s;
    }

    private void executeCommand(TestContext testContext, PlayerEntity player, String command) throws CommandSyntaxException {
        var scs = player.getCommandSource(testContext.getWorld());
        scs.getDispatcher().execute(command, scs);
    }
}
