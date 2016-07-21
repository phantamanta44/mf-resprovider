package io.github.phantamanta44.mobafort.mfrp.stat;

import io.github.phantamanta44.mobafort.mfrp.resource.ResourceTracker;
import io.github.phantamanta44.mobafort.weaponize.stat.AbstractStat;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.bukkit.entity.Player;

public abstract class MutableStat<T extends Number> extends AbstractStat<T> {

	public MutableStat(Player player, Stats type) {
		super(player, type);
	}

	@Override
	public boolean isMutable() {
		return super.isMutable();
	}

	@Override
	public abstract void setValue(T val);

	public static class HitPoints extends MutableStat<Integer> {

		public HitPoints(Player player) {
			super(player, Stats.HP);
		}

		@Override
		public Integer getValue() {
			return ResourceTracker.getHp(player);
		}

		@Override
		public void setValue(Integer val) {
			ResourceTracker.setHp(player, val, StatTracker.getStat(player, Stats.HP_MAX).getValue());
		}

	}

	public static class Mana extends MutableStat<Integer> {

		public Mana(Player player) {
			super(player, Stats.MANA);
		}

		@Override
		public Integer getValue() {
			return ResourceTracker.getMana(player);
		}

		@Override
		public void setValue(Integer val) {
			ResourceTracker.setMana(player, val, StatTracker.getStat(player, Stats.MANA_MAX).getValue());
		}

	}

}
