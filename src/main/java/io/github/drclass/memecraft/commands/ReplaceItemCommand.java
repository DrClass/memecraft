package io.github.drclass.memecraft.commands;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.drclass.memecraft.Memecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ReplaceItemCommand {
	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext buildContext) {
		return Commands.literal("replaceitem").requires(source -> source.hasPermission(2)).then(Commands.argument("player", EntityArgument.players())
				.then(Commands.argument("from", ItemArgument.item(buildContext))
						.then(Commands.argument("to", ItemArgument.item(buildContext)).executes(context -> replaceItems(context,
								EntityArgument.getPlayers(context, "player"), ItemArgument.getItem(context, "from"), ItemArgument.getItem(context, "to"))))));
	}

	private static int replaceItems(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, ItemInput from, ItemInput to) {
		for (ServerPlayer player : targets) {
			Inventory inventory = player.getInventory();
			for (int i = 0; i < inventory.getContainerSize(); i++) {
				ItemStack currentStack = inventory.getItem(i);
				if (!currentStack.isEmpty() && currentStack.getItem() == from.getItem()) {
					int stackSize = currentStack.getCount();
					ItemStack newStack = null;
					try {
						newStack = to.createItemStack(stackSize, false);
						inventory.setItem(i, newStack);
					} catch (CommandSyntaxException e) {
						// This should not be reachable
						Memecraft.LOGGER.error("Failed to replace item " + from.getItem() + " to " + to.getItem() + " in slot " + i + " for player "
								+ player.getDisplayName() + ".", e);
					}
				}
			}
		}
		return Command.SINGLE_SUCCESS;
	}
}
