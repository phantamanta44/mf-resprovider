package io.github.phantamanta44.mobafort.mfrp.status;

import io.github.phantamanta44.mobafort.lib.collection.DisposingNestedMap;
import io.github.phantamanta44.mobafort.lib.collection.TimedDecayMap;
import io.github.phantamanta44.mobafort.mfrp.RPPlugin;
import io.github.phantamanta44.mobafort.mfrp.event.MobaEventApplyCC;
import io.github.phantamanta44.mobafort.mfrp.stat.StatTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class StatusTracker {

    private static Map<String, IStatus> registry;
    private static DisposingNestedMap<UUID, IStatus, Integer, TimedDecayMap<IStatus>> statusMap;

    public static void init() {
        registry = new HashMap<>();
        statusMap = new DisposingNestedMap<>(() -> {
            TimedDecayMap<IStatus> tdm = new TimedDecayMap<>(RPPlugin.INSTANCE, 1L);
            tdm.onSelfMutation(() -> {
                Map.Entry<UUID, TimedDecayMap<IStatus>> entry = statusMap.entrySet().stream()
                        .filter(e -> e.getValue() == tdm)
                        .findAny().orElse(null);
                if (entry != null)
                    StatTracker.rescan(Bukkit.getPlayer(entry.getKey()), StatTracker.SRC_STATUS);
            });
            return tdm;
        });
    }

    public static void registerStatus(IStatus status) {
        registry.put(status.getId(), status);
    }

    public static void inflict(Player player, String id) {
        inflict(player, id, 1);
    }

    public static void inflict(Player player, String id, int amt) {
        inflict(player, id, amt, null);
    }

    public static void inflict(Player player, String id, int amt, Player src) {
        IStatus status = registry.get(id);
        if (status == null || amt < 1)
            throw new IllegalArgumentException();
        TimedDecayMap<IStatus> map = getOrCreateMap(player.getUniqueId());
        if (status instanceof ICCStatus) {
            CrowdControl cc = ((ICCStatus)status).getCrowdControl(player, amt);
            MobaEventApplyCC event = MobaEventApplyCC.fire(src, player, cc);
            if (event.isCancelled())
                return;
        }
        if (map.get(status) < status.getMaxStacks())
            map.add(status, amt, status.getDuration());
        else
            map.resetDecayTimer(status);
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
