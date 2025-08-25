package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.data.OperationResult;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import static com.github.breadbyte.itemcustomizer.server.Helper.IsValidJson;
import static com.github.breadbyte.itemcustomizer.server.Helper.JsonString2Text;

public class RenameOperations {
    public static OperationResult renameItem(ServerPlayerEntity player, String input) {
        var playerItem = player.getMainHandStack();
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

        return OperationResult.ok("Item renamed", SoundEvents.BLOCK_ANVIL_USE, 1);
    }

    public static OperationResult resetName(ServerPlayerEntity player) {
        var playerItem = player.getMainHandStack();

        playerItem.set(DataComponentTypes.CUSTOM_NAME, null);
        return OperationResult.ok("Item name reset to default!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
    }
}
