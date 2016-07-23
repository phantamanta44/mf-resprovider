package io.github.phantamanta44.mobafort.mfrp.item;

import io.github.phantamanta44.mobafort.lib.item.ItemSig;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class ItemRegistry {

	private static List<IItem> itemMap;

	public static void init() {
		itemMap = new LinkedList<>();
	}

	public static void register(IItem item) {
		itemMap.add(item);
	}

	public static IItem get(ItemStack stack) {
		return itemMap.stream()
				.filter(e -> e.getType().matches(stack))
				.findAny()
				.orElse(null);
	}

	public static IItem get(ItemSig sig) {
		return itemMap.stream()
				.filter(e -> e.getType().equals(sig))
				.findAny()
				.orElse(null);
	}

}
