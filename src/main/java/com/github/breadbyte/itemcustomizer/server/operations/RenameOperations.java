package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.jmx.Server;

import static com.github.breadbyte.itemcustomizer.server.util.Helper.IsValidJson;
import static com.github.breadbyte.itemcustomizer.server.util.Helper.JsonString2Text;

public class RenameOperations {
    public static Result<Void> renameItem(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        var playerItem = getPlayerItem.unwrap();

        var input = String.valueOf(ctx.getArgument(RenameCommand.RENAME_ARGUMENT, String.class));

        Text outputText;

        // CUSTOM_NAME will default to having italics on the text, so we remove that here.
        // The text will still style normally, it's just that we remove anything by default,
        // and whatever the user will pass on will be applied as is.

        // If the input is a valid JSON, we will use it as is.
        if (IsValidJson(input)) {
            outputText = Text.empty().setStyle(Style.EMPTY.withItalic(false)).append(JsonString2Text(input));
        } else {
            outputText = Text.literal(input).setStyle(Style.EMPTY.withItalic(false));
        }

        playerItem.set(DataComponentTypes.CUSTOM_NAME, outputText);

        return Result.ok();
    }

    public static Result<Void> resetName(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        var playerItem = getPlayerItem.unwrap();

        playerItem.set(DataComponentTypes.CUSTOM_NAME, null);
        return Result.ok();
    }
}
