package io.github.phantamanta44.mobafort.mfrp.item;

import io.github.phantamanta44.mobafort.lib.item.ItemSig;
import io.github.phantamanta44.mobafort.mfrp.stat.ProvidedStat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface IItem {

	String getId();

	ItemSig getType();

	void initialize(Player player, ItemStack stack);

	void update(long tick, Player player, ItemStack stack);

	List<ProvidedStat<?>> getCommonStats(Player player, ItemStack stack);

	Map<String, ProvidedStat<?>> getUniqueStats(Player player, ItemStack stack);

}
