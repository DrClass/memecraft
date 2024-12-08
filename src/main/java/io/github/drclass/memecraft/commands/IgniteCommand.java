package io.github.drclass.memecraft.commands;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class IgniteCommand {
	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("ignite").requires(source -> source.hasPermission(2))
				.then(Commands.argument("targets", EntityArgument.entities())
						.then(Commands.argument("duration", IntegerArgumentType.integer(1, 100)).executes(context -> igniteEntities(context,
								EntityArgument.getEntities(context, "targets"), IntegerArgumentType.getInteger(context, "duration")))));
	}

	private static int igniteEntities(CommandContext<CommandSourceStack> context, Collection<? extends Entity> targets, int duration) {
		for (Entity entity : targets) {
			entity.setRemainingFireTicks(duration * 20);
			context.getSource().sendSuccess(() -> Component.literal("Ignited " + entity.getName().getString() + " for " + duration + " seconds."), true);
		}
		return Command.SINGLE_SUCCESS;
	}
}
