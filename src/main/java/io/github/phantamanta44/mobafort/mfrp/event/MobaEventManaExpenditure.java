package io.github.phantamanta44.mobafort.mfrp.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobaEventManaExpenditure extends Event {

	private static final HandlerList hl = new HandlerList();

	private final Player player;
	private final int amount;

	public MobaEventManaExpenditure(Player player, int amount) {
		this.player = player;
		this.amount = amount;
	}

	public Player getPlayer() {
		return player;
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public HandlerList getHandlers() {
		return hl;
	}

	public static MobaEventManaExpenditure fire(Player player, int amount) {
		MobaEventManaExpenditure event = new MobaEventManaExpenditure(player, amount);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event;
	}

}
