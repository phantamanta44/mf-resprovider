package io.github.phantamanta44.mobafort.mfrp.status;

import io.github.phantamanta44.mobafort.mfrp.stat.ProvidedStat;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface IStatStatus extends IStatus {

	<T extends Number> Collection<ProvidedStat<T>> getProvidedStats(Player player, int stacks);

}
