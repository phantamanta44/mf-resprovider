package io.github.phantamanta44.mobafort.mfrp.event;

import io.github.phantamanta44.mobafort.mfrp.stat.StatTracker;
import io.github.phantamanta44.mobafort.weaponize.stat.Damage;
import io.github.phantamanta44.mobafort.weaponize.stat.IStatted;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MobaEventDamageByPlayer extends MobaEventDamage {

    private static final HandlerList hl = new HandlerList();

    public static HandlerList getHandlerList() {
        return hl;
    }

    private final Player source;

    public MobaEventDamageByPlayer(Player source, LivingEntity target, Damage damage) {
        super(new IStatted() {
            @Override
            public <T extends Number> T getStat(Stats<T> s) {
                return StatTracker.getStat(source, s).getValue();
            }
        }, target, damage);
        this.source = source;
    }

    public Player getPlayerSource() {
        return source;
    }

    @Override
    public HandlerList getHandlers() {
        return hl;
    }

    public static MobaEventDamageByPlayer fire(Player source, LivingEntity target, Damage damage) {
        MobaEventDamageByPlayer event = new MobaEventDamageByPlayer(source, target, damage);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

}
