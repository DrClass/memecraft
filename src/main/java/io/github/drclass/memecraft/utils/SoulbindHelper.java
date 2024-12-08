package io.github.drclass.memecraft.utils;

import java.util.HashMap;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import io.github.drclass.memecraft.component.MemecraftDataComponentTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;

public class SoulbindHelper {
	private static final HashMap<Player, SoulbindHelper> handlerMap = new HashMap<Player, SoulbindHelper>();
	private static final String SOULBOUND_ITEMS_LIST = "soulboundItemsList";
	private final Player player;

	public static SoulbindHelper getOrCreateSoulboundHelper(Player player) {
		return getSoulbindHelper(player) != null ? getSoulbindHelper(player) : createSoulbindHelper(player);
	}

	@Nullable
	public static SoulbindHelper getSoulbindHelper(Player player) {
		return handlerMap.get(player);
	}

	public static SoulbindHelper createSoulbindHelper(Player player) {
		SoulbindHelper newHandler = new SoulbindHelper(player);
		handlerMap.put(player, newHandler);
		return newHandler;
	}

	public static boolean hasRetainedItems(Player player) {
		return player.getPersistentData().contains(SOULBOUND_ITEMS_LIST);
	}

	private SoulbindHelper(Player playerIn) {
		this.player = playerIn;
	}
	
	public void retainInventory(Inventory inventory) {
		ListTag soulboundItems = new ListTag();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (!inventory.getItem(i).isEmpty() && inventory.getItem(i).getComponents().getOrDefault(MemecraftDataComponentTypes.SOULBIND.get(), 0) > 0) {
				ItemStack item = inventory.getItem(i);
				item.set(MemecraftDataComponentTypes.SOULBIND.get(), item.get(MemecraftDataComponentTypes.SOULBIND.get()) - 1);
				Optional<JsonElement> json = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, item).resultOrPartial();
				if (json.isPresent()) {
					soulboundItems.add(StringTag.valueOf(json.get().toString()));
					inventory.setItem(i, ItemStack.EMPTY);
				} else {
					// This should not be reachable
					soulboundItems.add(StringTag.valueOf(""));
				}
			} else {
				// Item is not soulbound or there is no item in slot, still need an empty item to take up a space.
				soulboundItems.add(StringTag.valueOf(""));
			}
		}
		player.getPersistentData().put(SOULBOUND_ITEMS_LIST, soulboundItems);
	}
	
	public void transferInventoryItems(Player respawnedPlayer) {
		ListTag soulboundItems = player.getPersistentData().getList(SOULBOUND_ITEMS_LIST, Tag.TAG_STRING);
		for (int i = 0; i < respawnedPlayer.getInventory().getContainerSize(); i++) {
			String json = soulboundItems.get(i).getAsString();
			if (json.isEmpty() || json.equals("")) {
				continue;
			}
			DataResult<Pair<ItemStack, JsonElement>> decodedResult = ItemStack.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(json));
			if (decodedResult.isSuccess() && decodedResult.resultOrPartial().isPresent()) {
				respawnedPlayer.getInventory().setItem(i, decodedResult.resultOrPartial().get().getFirst());
			}
		}
		handlerMap.remove(player);
	}
}
