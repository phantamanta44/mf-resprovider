package io.github.phantamanta44.mobafort.mfrp.status;

import org.bukkit.entity.Player;

public interface ICCStatus extends IStatus {

	CrowdControl getCrowdControl(Player player, int stacks);

}
