package io.github.phantamanta44.mobafort.mfrp.resource;

import io.github.phantamanta44.mobafort.mfrp.event.MobaEventDamage;
import io.github.phantamanta44.mobafort.mfrp.event.MobaEventHeal;
import io.github.phantamanta44.mobafort.mfrp.stat.IStatted;
import io.github.phantamanta44.mobafort.mfrp.stat.StatTracker;
import io.github.phantamanta44.mobafort.weaponize.stat.Damage;
import io.github.phantamanta44.mobafort.weaponize.stat.IDamageProvider;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.apache.commons.lang.mutable.MutableDouble;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DamageProvider implements IDamageProvider {

	@Override
	public void damageEntity(Damage dmg, Player src, LivingEntity target) {
		MobaEventDamage event = MobaEventDamage.fire(src, target, dmg);
		if (event.isCancelled())
			return;
		MutableDouble amt = new MutableDouble(dmg.getBaseDmg());
		dmg.getDamages().forEach(e -> amt.add(e.getValue() * StatTracker.getStat(src, e.getKey()).getValue().doubleValue()));
		if (Math.random() <= StatTracker.getStat(src, Stats.CRIT_CHANCE).getValue())
			amt.setValue(amt.doubleValue() * (2D + StatTracker.getStat(src, Stats.CRIT_DMG).getValue()));
		if (target instanceof Player) {
			Player tgt = (Player)target;
			if (dmg.getType() != Damage.DamageType.TRUE) {
				int dmgRed = 0;
				float steal = 0F;
				switch (dmg.getType()) {
					case PHYSICAL:
						dmgRed = StatTracker.getStat(tgt, Stats.ARM).getValue() - StatTracker.getStat(src, Stats.ARM_PEN).getValue();
						steal = StatTracker.getStat(src, Stats.LIFE_STEAL).getValue();
						break;
					case MAGIC:
						dmgRed = StatTracker.getStat(tgt, Stats.MR).getValue() - StatTracker.getStat(src, Stats.MAG_PEN).getValue();
						steal = StatTracker.getStat(src, Stats.SPELL_VAMP).getValue();
						break;
				}
				if (dmgRed >= 0)
					amt.setValue(amt.doubleValue() * 100 / (100 + dmgRed));
				else
					amt.setValue(amt.doubleValue() * (2 - (100 / (100 - dmgRed))));
				ResourceTracker.addHp(src, (int)(amt.intValue() * steal), StatTracker.getStat(src, Stats.HP_MAX).getValue());
			}
			ResourceTracker.addHp(tgt, -amt.intValue(), StatTracker.getStat(tgt, Stats.HP_MAX).getValue());
		} else if (target instanceof IStatted) {
			IStatted tgt = (IStatted)target;
			if (dmg.getType() != Damage.DamageType.TRUE) {
				int dmgRed = 0;
				float steal = 0F;
				switch (dmg.getType()) {
					case PHYSICAL:
						dmgRed = tgt.getStat(Stats.ARM) - StatTracker.getStat(src, Stats.ARM_PEN).getValue();
						steal = StatTracker.getStat(src, Stats.LIFE_STEAL).getValue();
						break;
					case MAGIC:
						dmgRed = tgt.getStat(Stats.MR) - StatTracker.getStat(src, Stats.MAG_PEN).getValue();
						steal = StatTracker.getStat(src, Stats.SPELL_VAMP).getValue();
						break;
				}
				if (dmgRed >= 0)
					amt.setValue(amt.doubleValue() * 100 / (100 + dmgRed));
				else
					amt.setValue(amt.doubleValue() * (2 - (100 / (100 - dmgRed))));
				ResourceTracker.addHp(src, (int)(amt.intValue() * steal), StatTracker.getStat(src, Stats.HP_MAX).getValue());
			}
			target.damage(amt.intValue(), src);
		}
		else
			target.damage(amt.doubleValue() / 5D, src);
	}

	@Override
	public void healEntity(double amountDouble, Player src, LivingEntity target) {
		int amt = (int)amountDouble;
		MobaEventHeal event = MobaEventHeal.fire(src, target, amt);
		if (event.isCancelled())
			return;
		if (target instanceof Player)
			ResourceTracker.addHp((Player) target, event.getAmount(), StatTracker.getStat((Player) target, Stats.HP_MAX).getValue());
		else if (target instanceof IStatted)
			target.setHealth(Math.min(target.getHealth() + amt, target.getMaxHealth()));
		else
			target.setHealth(Math.min(target.getHealth() + amt / 5D, target.getMaxHealth()));
	}

}
