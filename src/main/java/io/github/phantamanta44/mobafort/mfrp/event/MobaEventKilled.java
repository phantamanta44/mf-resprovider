package io.github.phantamanta44.mobafort.mfrp.event;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MobaEventKilled extends Event {

    private static final HandlerList hl = new HandlerList();

    public static HandlerList getHandlerList() {
        return hl;
    }

    private final Object source;
    private final Player target;
    private final List<Object> assists;
    private boolean cancelled;

    public MobaEventKilled(Object source, Player target, Collection<Object> assists) {
        this.source = source;
        this.target = target;
        this.assists = Lists.newArrayList(assists);
    }

    public Object getSource() {
        return source;
    }

    public Player getTarget() {
        return target;
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

    public static MobaEventKilled fire(Object source, Player target) {
        return fire(source, target, Collections.emptyList());
    }

    public static MobaEventKilled fire(Object source, Player target, Collection<Object> assists) {
        MobaEventKilled event = new MobaEventKilled(source, target, assists);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

}
