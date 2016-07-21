package io.github.phantamanta44.mobafort.mfrp.stat;

import io.github.phantamanta44.mobafort.weaponize.stat.AbstractStat;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.bukkit.entity.Player;

public class ImmutableStat<T extends Number> extends AbstractStat<T> {

	private T value;

	public ImmutableStat(Player player, Stats<T> type, T value) {
		super(player, type);
		this.value = value;
	}

	@Override
	public T getValue() {
		return value;
	}

}
