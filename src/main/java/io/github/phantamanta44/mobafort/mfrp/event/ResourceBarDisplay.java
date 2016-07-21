package io.github.phantamanta44.mobafort.mfrp.event;

import io.github.phantamanta44.mobafort.mfrp.resource.ResourceTracker;
import io.github.phantamanta44.mobafort.mfrp.stat.StatTracker;
import io.github.phantamanta44.mobafort.weaponize.Weaponize;
import io.github.phantamanta44.mobafort.weaponize.stat.IStat;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongConsumer;

public class ResourceBarDisplay implements Listener, LongConsumer {

	private final Map<UUID, BossBar> manaBars;

	public ResourceBarDisplay() {
		Weaponize.INSTANCE.registerTickHandler(this);
		manaBars = new HashMap<>();
	}

	@Override
	public void accept(long tick) {
		if (tick % 2 == 0) {
			Bukkit.getServer().getOnlinePlayers().forEach(p -> {
				ResourceTracker.ResourceInfo ri = ResourceTracker.getInfo(p);
				IStat<Integer> maxHp = StatTracker.getStat(p, Stats.HP_MAX);
				IStat<Integer> maxMana = StatTracker.getStat(p, Stats.MANA_MAX);
				p.setLevel(ri.getHp());
				float hpPerc = (float)ri.getHp() / maxHp.getValue().floatValue();
				p.setExp(0.99F * hpPerc);
				p.setHealth(p.getMaxHealth() * hpPerc);
				BossBar bar = getOrCreateBossBar(p);
				bar.setTitle(Integer.toString(ri.getMana()));
				bar.setProgress(0.01D + 0.99D * ((double)ri.getMana() / maxMana.getValue().doubleValue()));
				bar.setStyle(getManaBarSegs(maxMana.getValue()));
			});
		}
	}

	public void uncache(Player player) {
		manaBars.remove(player.getUniqueId());
	}

	private BossBar getOrCreateBossBar(Player p) {
		BossBar bar = manaBars.get(p);
		if (bar == null) {
			bar = Bukkit.getServer().createBossBar("0", BarColor.BLUE, BarStyle.SEGMENTED_6);
			bar.addPlayer(p);
			bar.setVisible(true);
			manaBars.put(p.getUniqueId(), bar);
		}
		return bar;
	}

	private BarStyle getManaBarSegs(int max) {
		if (max > 2000)
			return BarStyle.SEGMENTED_20;
		else if (max > 1200)
			return BarStyle.SEGMENTED_12;
		else if (max > 1000)
			return BarStyle.SEGMENTED_10;
		return BarStyle.SEGMENTED_6;
	}

}
