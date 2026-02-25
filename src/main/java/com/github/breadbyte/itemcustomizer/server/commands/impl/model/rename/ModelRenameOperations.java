package com.github.breadbyte.itemcustomizer.server.commands.impl.model.rename;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.rename.IModelRenameOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.rename.ModelRenameParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import static com.github.breadbyte.itemcustomizer.server.util.Helper.IsValidJson;
import static com.github.breadbyte.itemcustomizer.server.util.Helper.JsonString2Text;

public class ModelRenameOperations implements IModelRenameOperations {
    @Override
    public Result<String> apply(ModelRenameParams params) {
        var playerItem = params.item();
        var input = params.name();

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

    @Override
    public Result<String> reset(ModelRenameParams params) {
        var playerItem = params.item();
        playerItem.remove(DataComponentTypes.CUSTOM_NAME);
        return Result.ok();
    }
}
