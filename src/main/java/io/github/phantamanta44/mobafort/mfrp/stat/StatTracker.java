package io.github.phantamanta44.mobafort.mfrp.stat;

import io.github.phantamanta44.mobafort.lib.collection.CollectionUtils;
import io.github.phantamanta44.mobafort.lib.collection.OneToManyMap;
import io.github.phantamanta44.mobafort.lib.math.MathUtils;
import io.github.phantamanta44.mobafort.mfrp.status.IStatStatus;
import io.github.phantamanta44.mobafort.mfrp.status.StatusTracker;
import io.github.phantamanta44.mobafort.weaponize.stat.IStat;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.apache.commons.lang.mutable.MutableDouble;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatTracker {

	public static final int SRC_ITEM = 0b001;
	public static final int SRC_STATUS = 0b010;
	public static final int SRC_BASE = 0b100;

	private static Map<UUID, StatCache> cached;

	public static void init() {
		cached = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Number> IStat<T> getStat(Player player, Stats<T> stat) {
		switch (stat.enumType) {
			case MANA:
				return (IStat<T>)new MutableStat.Mana(player);
			case HP:
				return (IStat<T>)new MutableStat.HitPoints(player);
			case HP_MISSING:
				return new ImmutableStat<T>(player, stat, (T)Integer.valueOf(getStat(player, Stats.HP_MAX).getValue() - getStat(player, Stats.HP).getValue()));
			default:
				return getCached(player).getStat(stat);
		}
	}

	public static void rescan(Player player, int toUpdate) {
		StatCache cache = cached.get(player);
		if (cache == null)
			aggregate(player);
		else
			cache.aggregate(toUpdate);
	}

	public static void uncache(Player player) {
		cached.remove(player.getUniqueId());
	}

	private static StatCache getCached(Player player) {
		StatCache cache = cached.get(player.getUniqueId());
		return cache != null ? cache : aggregate(player);
	}

	private static StatCache aggregate(Player player) {
		StatCache cache = new StatCache(player);
		cached.put(player.getUniqueId(), cache);
		return cache;
	}

	private static class StatCache {

		private final Player player;
		private final Map<Stats.StatType, Number> statMap;
		private final Map<Stats.StatType, ProvidedStat<?>> providerMap;

		private StatCache(Player player) {
			this.player = player;
			this.statMap = new HashMap<>();
			this.providerMap = new HashMap<>();
			this.aggregate(SRC_ITEM | SRC_STATUS | SRC_BASE);
		}

		@SuppressWarnings("unchecked")
		private <T extends Number> IStat<T> getStat(Stats<T> stat) {
			Number val = statMap.get(stat.enumType);
			return val != null ? new ImmutableStat<>(player, stat, (T)val) : new ImmutableStat(player, stat, 0);
		}

		private void aggregate(int goal) {
			if ((goal & SRC_ITEM) != 0) {
				providerMap.entrySet().removeIf(e -> (e.getValue().src & SRC_ITEM) != 0);
				// TODO Implement
			}
			if ((goal & SRC_STATUS) != 0) {
				providerMap.entrySet().removeIf(e -> (e.getValue().src & SRC_STATUS) != 0);
				StatusTracker.getStatus(player).stream()
						.filter(e -> e.getKey() instanceof IStatStatus)
						.forEach(e -> ((IStatStatus)e.getKey()).getProvidedStats(player, e.getValue()).forEach(this::addProvider));
			}
			if ((goal & SRC_BASE) != 0) {
				providerMap.entrySet().removeIf(e -> (e.getValue().src & SRC_BASE) != 0);
				// TODO Implement
			}
			reduceProviderValues();
		}

		private void reduceProviderValues() {
			statMap.clear();
			CollectionUtils.groupByProperty(providerMap.entrySet(), Map.Entry::getKey).forEach((k, v) -> {
				MutableDouble val = new MutableDouble();
				OneToManyMap<ProvidedStat.ReduceType, ProvidedStat<?>, List<ProvidedStat<?>>> rt = CollectionUtils.groupByProperty(v.stream().map(Map.Entry::getValue).collect(Collectors.toList()), e -> e.type);
				List<ProvidedStat<?>> add = rt.get(ProvidedStat.ReduceType.ADD);
				if (add != null)
					add.forEach(s -> val.add(s.value));
				List<ProvidedStat<?>> percent = rt.get(ProvidedStat.ReduceType.PERC);
				if (percent != null) {
					MutableDouble ttlPct = new MutableDouble();
					percent.forEach(s -> ttlPct.add(s.value));
					val.setValue(val.doubleValue() * ttlPct.doubleValue());
				}
				List<ProvidedStat<?>> multiply = rt.get(ProvidedStat.ReduceType.MULT);
				if (multiply != null)
					multiply.forEach(s -> val.setValue(val.doubleValue() * s.value.doubleValue()));
				statMap.put(k, MathUtils.box(val.doubleValue(), k.type));
			});
		}

		private void addProvider(ProvidedStat ps) {
			providerMap.put(ps.stat.enumType, ps);
		}

	}

}
