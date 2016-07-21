package io.github.phantamanta44.mobafort.mfrp.status;

import io.github.phantamanta44.mobafort.lib.collection.DisposingNestedMap;
import io.github.phantamanta44.mobafort.lib.collection.TimedDecayMap;
import io.github.phantamanta44.mobafort.mfrp.RPPlugin;
import org.bukkit.entity.Player;

import java.util.*;

public class StatusTracker {

	private static Map<String, IStatus> registry;
	private static DisposingNestedMap<UUID, IStatus, Integer, TimedDecayMap<IStatus>> statusMap;

	public static void init() {
		registry = new HashMap<>();
		statusMap = new DisposingNestedMap<>(() -> new TimedDecayMap<>(RPPlugin.INSTANCE, 1L));
	}

	public static void registerStatus(IStatus status) {
		registry.put(status.getId(), status);
	}

	public static void inflict(Player player, String id) {
		inflict(player, id, 1);
	}

	public static void inflict(Player player, String id, int amt) {
		IStatus status = registry.get(id);
		if (status == null || amt < 1)
			throw new IllegalArgumentException();
		getOrCreateMap(player.getUniqueId()).add(status, amt);
	}

	public static boolean hasStatus(Player player, String id) {
		IStatus status = registry.get(id);
		if (status == null)
			throw new IllegalArgumentException();
		TimedDecayMap<IStatus> map = statusMap.get(player.getUniqueId());
		return map != null && map.contains(status);
	}

	public static int getStacks(Player player, String id) {
		IStatus status = registry.get(id);
		if (status == null)
			throw new IllegalArgumentException();
		TimedDecayMap<IStatus> map = statusMap.get(player.getUniqueId());
		return map != null ? map.get(status) : 0;
	}

	public static Set<Map.Entry<IStatus, Integer>> getStatus(Player player) {
		TimedDecayMap<IStatus> map = statusMap.get(player.getUniqueId());
		return map != null ? map.entrySet() : Collections.emptySet();
	}

	private static TimedDecayMap<IStatus> getOrCreateMap(UUID player) {
		TimedDecayMap<IStatus> map = statusMap.get(player);
		if (map == null) {
			map = new TimedDecayMap<>(RPPlugin.INSTANCE, 1L);
			statusMap.put(player, map);
		}
		return map;
	}

}
