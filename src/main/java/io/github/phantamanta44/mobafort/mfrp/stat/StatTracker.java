package io.github.phantamanta44.mobafort.mfrp.stat;

import io.github.phantamanta44.mobafort.lib.collection.CollectionUtils;
import io.github.phantamanta44.mobafort.lib.collection.OneToManyMap;
import io.github.phantamanta44.mobafort.lib.math.MathUtils;
import io.github.phantamanta44.mobafort.mfrp.item.ItemTracker;
import io.github.phantamanta44.mobafort.mfrp.resource.ResourceTracker;
import io.github.phantamanta44.mobafort.mfrp.status.IStatStatus;
import io.github.phantamanta44.mobafort.mfrp.status.StatusTracker;
import io.github.phantamanta44.mobafort.weaponize.stat.IStat;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.apache.commons.lang.mutable.MutableDouble;
import org.apache.commons.lang.mutable.MutableFloat;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
				return new ImmutableStat<>(player, stat, (T)Integer.valueOf(getStat(player, Stats.HP_MAX).getValue() - getStat(player, Stats.HP).getValue()));
			case AD:
				return new ImmutableStat<>(player, stat, (T)Integer.valueOf(getCached(player).getStat(Stats.AD).getValue() + getStat(player, Stats.BONUS_AD).getValue()));
			default:
				return getCached(player).getStat(stat);
		}
	}

	public static void rescan(Player player, int toUpdate) {
		StatCache cache = cached.get(player.getUniqueId());
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
		private final OneToManyMap<Stats.StatType, ProvidedStat<?>, List<ProvidedStat<?>>> providerMap;

		private StatCache(Player player) {
			this.player = player;
			this.statMap = new HashMap<>();
			this.providerMap = new OneToManyMap<>(CopyOnWriteArrayList::new);
			this.aggregate(SRC_ITEM | SRC_STATUS | SRC_BASE);
		}

		@SuppressWarnings("unchecked")
		private <T extends Number> IStat<T> getStat(Stats<T> stat) {
			Number val = statMap.get(stat.enumType);
			return new ImmutableStat<>(player, stat, val != null ? (T)val : (T)reduceFromProviders(stat));
		}

		private void aggregate(int goal) {
			if ((goal & SRC_ITEM) != 0) {
				removeProviders(SRC_ITEM);
				Set<String> seen = new HashSet<>();
				ItemTracker.get(player).forEach(i -> {
					i.getValue().getCommonStats(player, i.getKey()).forEach(this::addProvider);
					i.getValue().getUniqueStats(player, i.getKey()).entrySet().stream()
							.filter(e -> !seen.contains(e.getKey()))
							.peek(e -> seen.add(e.getKey()))
							.map(Map.Entry::getValue)
							.forEach(this::addProvider);
				});
			}
			if ((goal & SRC_STATUS) != 0) {
				removeProviders(SRC_STATUS);
				StatusTracker.getStatus(player).stream()
						.filter(e -> e.getKey() instanceof IStatStatus)
						.forEach(e -> ((IStatStatus)e.getKey()).getProvidedStats(player, e.getValue()).forEach(this::addProvider));
			}
			if ((goal & SRC_BASE) != 0) {
				removeProviders(SRC_BASE);
				addProvider(new ProvidedStat<>(Stats.HP_MAX, 3100, SRC_BASE, ProvidedStat.ReduceType.ADD));
				addProvider(new ProvidedStat<>(Stats.MANA_MAX, 3000, SRC_BASE, ProvidedStat.ReduceType.ADD));
				addProvider(new ProvidedStat<>(Stats.AP, 250, SRC_BASE, ProvidedStat.ReduceType.ADD));
				addProvider(new ProvidedStat<>(Stats.AD, 151, SRC_BASE, ProvidedStat.ReduceType.ADD));
				addProvider(new ProvidedStat<>(Stats.MOVE_SPEED, 460, SRC_BASE, ProvidedStat.ReduceType.ADD));
				// TODO Implement
			}
			ResourceTracker.capResources(player, getStat(Stats.HP_MAX).getValue(), getStat(Stats.MANA_MAX).getValue());
		}

		private <T extends Number> Number reduceFromProviders(Stats<T> stat) {
			List<ProvidedStat<?>> provs = providerMap.get(stat.enumType);
			if (provs == null)
				return MathUtils.box(0, stat.enumType.type);
			OneToManyMap<ProvidedStat.ReduceType, ProvidedStat<?>, List<ProvidedStat<?>>> byType = CollectionUtils.groupByProperty(provs, p -> p.type);
			if (byType.contains(ProvidedStat.ReduceType.ADD)) {
				MutableDouble val = new MutableDouble();
				byType.get(ProvidedStat.ReduceType.ADD).forEach(p -> val.add(p.value));
				if (byType.contains(ProvidedStat.ReduceType.PERC)) {
					MutableFloat perc = new MutableFloat();
					byType.get(ProvidedStat.ReduceType.PERC).forEach(p -> perc.add(p.value));
					val.setValue(val.doubleValue() * (1 + perc.doubleValue()));
				}
				if (byType.contains(ProvidedStat.ReduceType.MULT))
					byType.get(ProvidedStat.ReduceType.MULT).forEach(p -> val.setValue(val.doubleValue() * p.value.doubleValue()));
				Number boxed = MathUtils.box(val.doubleValue(), stat.enumType.type);
				statMap.put(stat.enumType, boxed);
				return boxed;
			}
			return MathUtils.box(0, stat.enumType.type);
		}

		private void addProvider(ProvidedStat ps) {
			providerMap.put(ps.stat.enumType, ps);
			statMap.remove(ps.stat.enumType);
		}

		private void removeProviders(int src) {
			providerMap.forEach((k, v) -> v.removeIf(e -> {
				if ((e.src & src) != 0) {
					statMap.remove(e.stat.enumType);
					return true;
				}
				return false;
			}));
		}

	}

}
