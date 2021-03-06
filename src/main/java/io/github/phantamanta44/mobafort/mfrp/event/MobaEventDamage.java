package io.github.phantamanta44.mobafort.mfrp.event;

import io.github.phantamanta44.mobafort.weaponize.stat.Damage;
import io.github.phantamanta44.mobafort.weaponize.stat.IStatted;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobaEventDamage extends Event {

    private static final HandlerList hl = new HandlerList();

    public static HandlerList getHandlerList() {
        return hl;
    }

    private final IStatted source;
    private final LivingEntity target;
    private final Damage damage;
    private boolean cancelled;

    public MobaEventDamage(IStatted source, LivingEntity target, Damage damage) {
        this.source = source;
        this.target = target;
        this.damage = damage;
        this.cancelled = false;
    }

    public IStatted getSource() {
        return source;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public Damage getDamage() {
        return damage;
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

    public static MobaEventDamage fire(IStatted source, LivingEntity target, Damage damage) {
        MobaEventDamage event = new MobaEventDamage(source, target, damage);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

}
