package io.github.phantamanta44.mobafort.mfrp.event;

import io.github.phantamanta44.mobafort.mfrp.status.CrowdControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobaEventApplyCC extends Event {

	private static final HandlerList hl = new HandlerList();

	public static HandlerList getHandlerList() {
		return hl;
	}

	private Player target, source;
	private CrowdControl type;
	private boolean cancelled;

	public MobaEventApplyCC(Player source, Player target, CrowdControl type) {
		this.target = target;
		this.source = source;
		this.type = type;
		this.cancelled = false;
	}

	public Player getTarget() {
		return target;
	}

	public Player getSource() {
		return source;
	}

	public CrowdControl getCC() {
		return type;
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

	public static MobaEventApplyCC fire(Player source, Player target, CrowdControl type) {
		MobaEventApplyCC event = new MobaEventApplyCC(source, target, type);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event;
	}

}
