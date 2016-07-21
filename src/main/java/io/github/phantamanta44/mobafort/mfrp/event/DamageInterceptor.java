package io.github.phantamanta44.mobafort.mfrp.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageInterceptor implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM && event.getEntity() instanceof Player)
			event.setCancelled(true);
	}

}
