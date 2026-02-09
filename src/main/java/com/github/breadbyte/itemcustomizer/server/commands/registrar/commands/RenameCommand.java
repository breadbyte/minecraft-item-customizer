package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.CommandDefinition;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class RenameCommand implements CommandDefinition<ServerCommandSource> {
    @Override
    public String commandName() {
        return "rename";
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {

        var cmd_root = literal(commandName());

        var builder = root.then(cmd_root
                .requires(Permissions.require(Check.Permission.RENAME.getPermission(), 4)
                        .or(scs -> scs.getPlayer().isCreative())));

    }
}
