package io.github.phantamanta44.mobafort.mfrp.item;

import io.github.phantamanta44.mobafort.mfrp.stat.StatTracker;
import io.github.phantamanta44.mobafort.weaponize.Weaponize;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ItemTracker {

	private static Map<UUID, Integer> counts;

	public static void init() {
		ItemRegistry.init();
		counts = new ConcurrentHashMap<>();
		Weaponize.INSTANCE.registerTickHandler(tick -> {
			Bukkit.getServer().getOnlinePlayers().forEach(p -> {
				MutableInt count = new MutableInt();
				Arrays.stream(p.getInventory().getContents())
						.filter(Objects::nonNull)
						.forEach(i -> {
							IItem item = ItemRegistry.get(i);
							if (item != null) {
								count.increment();
								if (!i.hasItemMeta() || !i.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
									item.initialize(p, i);
									ItemMeta meta;
									if (i.hasItemMeta())
										meta = i.getItemMeta();
									else
										meta = Bukkit.getServer().getItemFactory().getItemMeta(i.getType());
									meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
									i.setItemMeta(meta);
								}
								item.update(tick, p, i);
							}
						});
				Integer prevCount = counts.get(p.getUniqueId());
				if (prevCount != null && count.intValue() != prevCount)
					StatTracker.rescan(p, StatTracker.SRC_ITEM);
				counts.put(p.getUniqueId(), count.intValue());
			});
		});
	}

	public static Collection<Map.Entry<ItemStack, IItem>> get(Player player) {
		return Arrays.stream(player.getInventory().getContents())
				.filter(Objects::nonNull)
				.map(i -> new AbstractMap.SimpleImmutableEntry<>(i, ItemRegistry.get(i)))
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toList());
	}

}
