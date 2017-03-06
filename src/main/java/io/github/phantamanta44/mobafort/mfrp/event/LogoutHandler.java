package io.github.phantamanta44.mobafort.mfrp.event;

import io.github.phantamanta44.mobafort.mfrp.RPPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogoutHandler implements Listener {

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        RPPlugin.INSTANCE.onLogout(event.getPlayer());
    }

}
