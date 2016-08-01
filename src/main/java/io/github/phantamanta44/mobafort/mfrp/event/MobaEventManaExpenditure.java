package io.github.phantamanta44.mobafort.mfrp.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobaEventManaExpenditure extends Event {

	private static final HandlerList hl = new HandlerList();

	private final Player player;
	private int amount;
	private boolean cancelled;

	public MobaEventManaExpenditure(Player player, int amount) {
		this.player = player;
		this.amount = amount;
		this.cancelled = false;
	}

	public Player getPlayer() {
		return player;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amt) {
		this.amount = amt;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
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
