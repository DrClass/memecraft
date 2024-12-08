package io.github.drclass.memecraft.events;

import io.github.drclass.memecraft.Memecraft;
import io.github.drclass.memecraft.component.MemecraftDataComponentTypes;
import io.github.drclass.memecraft.utils.RomanNumeralUtils;
import io.github.drclass.memecraft.utils.SoulbindHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Memecraft.MODID)
public class SoulbindEvents {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		int soulbindLevel = stack.getComponents().getOrDefault(MemecraftDataComponentTypes.SOULBIND.get(), 0);
		if (soulbindLevel > 0) {
			event.getToolTip().add(Component.literal("Soulbound " + RomanNumeralUtils.toRoman(soulbindLevel)));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			SoulbindHelper.getOrCreateSoulboundHelper(player).retainInventory(player.getInventory());
		}
	}

	@SubscribeEvent
	public static void itemTransferEvent(Clone event) {
		if (event.isWasDeath()) {
			Player oldPlayer = event.getOriginal();
			Player newPlayer = event.getEntity();
			if (SoulbindHelper.hasRetainedItems(oldPlayer)) {
				SoulbindHelper.getOrCreateSoulboundHelper(oldPlayer).transferInventoryItems(newPlayer);
			} else if (SoulbindHelper.hasRetainedItems(newPlayer)) {
				SoulbindHelper.getOrCreateSoulboundHelper(newPlayer).transferInventoryItems(newPlayer);
			}
		}
	}
}
