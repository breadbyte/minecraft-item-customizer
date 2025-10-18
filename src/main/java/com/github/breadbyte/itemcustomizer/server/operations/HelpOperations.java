package com.github.breadbyte.itemcustomizer.server.operations;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.net.URI;

public class HelpOperations {

    public static int LoreHelp(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.of("Usage: /lore <name>"), false);
        context.getSource().sendFeedback(() -> Text.of("Create custom lore using the website below:"), false);
        context.getSource().sendFeedback(() -> Text.literal("https://colorize.fun/en/minecraft").setStyle(Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create("https://colorize.fun/en/minecraft"))).withHoverEvent(new HoverEvent.ShowText(Text.of("Click to open the website.")))), false);

        return 1;
    }

    public static int RenameHelp(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.of("Usage: /rename <name>"), false);
        context.getSource().sendFeedback(() -> Text.of("Create custom names using the website below:"), false);
        context.getSource().sendFeedback(() -> Text.literal("https://colorize.fun/en/minecraft").setStyle(Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create("https://colorize.fun/en/minecraft"))).withHoverEvent(new HoverEvent.ShowText(Text.of("Click to open the website.")))), false);

        return 1;
    }
}
