package com.github.breadbyte.itemcustomizer.server.command;

import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.operations.ModelOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;

public class ModelCommands {
    // TODO: Cost is static

    public static int applyGlint(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.applyGlint(player);

        if (retval.isOk())
            Helper.ApplyCost(player, retval.getCost());
        else
            Helper.SendMessageNo(player, retval.details());
        return 1;
    }

    public static int removeGlint(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.removeGlint(player);

        if (retval.isOk())
            Helper.ApplyCost(player, retval.getCost());
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
        if (res.isOk()) {
            Helper.ApplyCost(player, res.getCost());
        } else
            Helper.SendMessageNo(player, res.details());

        return 1;
    }

    public static int resetModel(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.revertModel(player);

        if (retval.isOk())
            Helper.ApplyCost(player, retval.getCost());
        else
            Helper.SendMessageNo(player, retval.details());

        return 1;
    }
}
