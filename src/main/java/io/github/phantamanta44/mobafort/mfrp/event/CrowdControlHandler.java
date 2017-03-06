package io.github.phantamanta44.mobafort.mfrp.event;

import io.github.phantamanta44.mobafort.mfrp.status.CrowdControl;
import io.github.phantamanta44.mobafort.mfrp.status.IAutoAttackSpell;
import io.github.phantamanta44.mobafort.weaponize.Weaponize;
import io.github.phantamanta44.mobafort.weaponize.event.EventSpellCast;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.LongConsumer;

public class CrowdControlHandler implements Listener, LongConsumer {

    public CrowdControlHandler() {
        Weaponize.INSTANCE.registerTickHandler(this);
    }

    @EventHandler
    public void onCast(EventSpellCast event) {
        if (event.getSpell().getTemplate() instanceof IAutoAttackSpell) {
            if (CrowdControl.getControlState(event.getPlayer()).isAutoImpaired())
                event.setCancelled(true);
        }
        else if (CrowdControl.getControlState(event.getPlayer()).isCastImpaired())
            event.setCancelled(true);
    }

    @Override
    public void accept(long tick) {
        if (tick % 3 == 0) {
            Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(p -> CrowdControl.getControlState(p).isMoveImpaired())
                    .forEach(p -> {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3, -4, true, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3, 160, true, false));
                    });
        }
    }

}
