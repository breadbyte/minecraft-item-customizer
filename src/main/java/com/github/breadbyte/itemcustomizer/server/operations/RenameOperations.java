package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import static com.github.breadbyte.itemcustomizer.server.Helper.IsValidJson;
import static com.github.breadbyte.itemcustomizer.server.Helper.JsonString2Text;

public class RenameOperations {
    public static int renameItem(CommandContext<ServerCommandSource> context) {
        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        var input = String.valueOf(context.getArgument("name", String.class));
        Text outputText;

        // CUSTOM_NAME will default to having italics on the text, so we remove that here.
        // The text will still style normally, it's just that we remove anything by default,
        // and whatever the user will pass on will be applied as is.

        // If the input is a valid JSON, we will use it as is.
        if (IsValidJson(input)) {
            //context.getSource().sendFeedback(() -> Text.literal("Using JSON input for item name."), false);
            outputText = Text.empty().setStyle(Style.EMPTY.withItalic(false)).append(JsonString2Text(input));
        } else {
            //context.getSource().sendFeedback(() -> Text.literal("Using string literal for item name."), false);
            outputText = Text.literal(input).setStyle(Style.EMPTY.withItalic(false));
        }

        playerItem.set(DataComponentTypes.CUSTOM_NAME, outputText);

        Helper.SendMessage(player,Text.literal("Renamed to ").append(outputText), SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);
        return 1;
    }

    public static int resetName(CommandContext<ServerCommandSource> context) {
        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        // Very straightforward, just remove the component

        if (playerItem.getComponents().get(DataComponentTypes.CUSTOM_NAME) == null) {
            Helper.SendMessage(player, "This item is already using the default name!", SoundEvents.ENTITY_VILLAGER_NO);
            return 0;
        }

        playerItem.set(DataComponentTypes.CUSTOM_NAME, null);
        Helper.SendMessage(player, "Item name reset to default!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        return 1;
    }
}
