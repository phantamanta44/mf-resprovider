package io.github.phantamanta44.mobafort.mfrp.resource;

import io.github.phantamanta44.mobafort.mfrp.event.MobaEventDamage;
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
		MutableDouble amt = new MutableDouble(dmg.getBaseDmg());
		dmg.getDamages().forEach(e -> amt.add(e.getValue() * StatTracker.getStat(src, e.getKey()).getValue().doubleValue()));
		if (Math.random() <= StatTracker.getStat(src, Stats.CRIT_CHANCE).getValue())
			amt.setValue(amt.doubleValue() * 2D * (1D + StatTracker.getStat(src, Stats.CRIT_DMG).getValue()));
		if (!(target instanceof Player))
			target.damage(amt.doubleValue() / 5D, src);
		else {
			Player tgt = (Player) target;
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
		}
		MobaEventDamage.fire(src, target, dmg);
	}

	@Override
	public void healEntity(double amt, Player src, LivingEntity target) {

	}

}
