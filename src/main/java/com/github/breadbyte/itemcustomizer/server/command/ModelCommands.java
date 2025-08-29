package com.github.breadbyte.itemcustomizer.server.command;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.operations.ModelOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;

public class ModelCommands {
    // TODO: Cost is static

    public static int applyGlint(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.applyGlint(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            Helper.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());
        return 1;
    }

    public static int removeGlint(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.removeGlint(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            Helper.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());

        return 1;
    }

    public static int applyModel(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var itemType = String.valueOf(ctx.getArgument("item_type", String.class));
        var itemName = String.valueOf(ctx.getArgument("item_name", String.class));
        Integer color = null;
        Boolean changeEquippable = null;
        try { color = ctx.getArgument("color", Integer.class); } catch (Exception ignored) {}
        try { changeEquippable = ctx.getArgument("change_equippable_texture", Boolean.class); } catch (Exception ignored) {}

        var res = ModelOperations.applyModel(player, itemType, itemName, color, changeEquippable);
        if (res.ok()) {
            Helper.SendMessageYes(player, res.details());
            Helper.ApplyCost(player, res.cost());
        } else
            Helper.SendMessageNo(player, res.details());

        return 1;
    }

    public static int resetModel(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.revertModel(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            Helper.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());

        return 1;
    }

    public static int getPermissionNode(CommandContext<ServerCommandSource> ctx) {
        var itemType = String.valueOf(ctx.getArgument("item_type", String.class));
        var itemName = String.valueOf(ctx.getArgument("item_name", String.class));

        var res = ModelOperations.getPermissionNodeFor(itemType, itemName);
        if (res.ok()) {
            var node = Check.Permission.CUSTOMIZE.chain(res.details());
            ctx.getSource().sendFeedback(() -> Text.literal("Permission node:").append(Text.literal(node).setStyle(Style.EMPTY.withColor(Formatting.GREEN))), false);
            ctx.getSource().sendFeedback(() -> Text.literal("Click here to copy to clipboard").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, node)).withColor(Formatting.BLUE)), false);
        } else
            ctx.getSource().sendFeedback(() -> Text.of(res.details()), false);

        return 1;
    }
}
