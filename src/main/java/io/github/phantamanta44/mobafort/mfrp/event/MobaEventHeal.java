package io.github.phantamanta44.mobafort.mfrp.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobaEventHeal extends Event {

	private static final HandlerList hl = new HandlerList();

	public static HandlerList getHandlerList() {
		return hl;
	}

	private final Player source;
	private final LivingEntity target;
	private int amount;
	private boolean cancelled;

	public MobaEventHeal(Player source, LivingEntity target, int amount) {
		this.source = source;
		this.target = target;
		this.amount = amount;
		this.cancelled = false;
	}

	public Player getSource() {
		return source;
	}

	public LivingEntity getTarget() {
		return target;
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

	public static MobaEventHeal fire(Player source, LivingEntity target, int amt) {
		MobaEventHeal event = new MobaEventHeal(source, target, amt);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event;
	}

}
