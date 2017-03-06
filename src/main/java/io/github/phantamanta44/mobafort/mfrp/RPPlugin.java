package io.github.phantamanta44.mobafort.mfrp;

import io.github.phantamanta44.mobafort.mfrp.event.*;
import io.github.phantamanta44.mobafort.mfrp.item.ItemTracker;
import io.github.phantamanta44.mobafort.mfrp.resource.DamageProvider;
import io.github.phantamanta44.mobafort.mfrp.resource.ResourceTracker;
import io.github.phantamanta44.mobafort.mfrp.stat.StatTracker;
import io.github.phantamanta44.mobafort.mfrp.status.StatusTracker;
import io.github.phantamanta44.mobafort.weaponize.stat.Damage;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class RPPlugin extends JavaPlugin {

    public static RPPlugin INSTANCE;

    private ResourceBarDisplay rbd;

    @Override
    public void onEnable() {
        INSTANCE = this;
        Damage.bindProvider(new DamageProvider());
        Stats.bindProvider(StatTracker::getStat);
        StatTracker.init();
        StatusTracker.init();
        ResourceTracker.init();
        ItemTracker.init();
        Bukkit.getServer().getPluginManager().registerEvents(new DamageInterceptor(), this);
        Bukkit.getServer().getPluginManager().registerEvents(rbd = new ResourceBarDisplay(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LogoutHandler(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CrowdControlHandler(), this);
        getCommand("mfrp").setExecutor(new RPCommandExecutor());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        rbd.cleanUp();
    }

    public void onLogout(Player p) {
        ResourceTracker.uncache(p);
        StatTracker.uncache(p);
        rbd.uncache(p);
    }

}
