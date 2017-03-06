package io.github.phantamanta44.mobafort.mfrp.event;

import io.github.phantamanta44.mobafort.mfrp.item.ItemRegistry;
import io.github.phantamanta44.mobafort.mfrp.stat.StatTracker;
import io.github.phantamanta44.mobafort.weaponize.stat.IStat;
import io.github.phantamanta44.mobafort.weaponize.stat.Stats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RPCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!permCheckFailed(sender)) {
            if (args.length == 0)
                msgHelpNoArgs(sender);
            else {
                switch (args[0].toLowerCase()) {
                    case "stats":
                        cmdStats(sender);
                        break;
                    case "items":
                        cmdItems(sender);
                        break;
                    case "hp":
                    case "mana":
                        cmdMutate(sender, args);
                        break;
                    default:
                        msgHelpNoArgs(sender);
                        break;
                }
            }
        }
        return true;
    }

    private static void cmdStats(CommandSender sender) {
        if (playerCheckFailed(sender))
            return;
        Player player = (Player)sender;
        List<String> msg = new ArrayList<>();
        Arrays.stream(Stats.class.getFields())
                .filter(f -> (f.getModifiers() & Modifier.STATIC) != 0 && Stats.class.isAssignableFrom(f.getType()))
                .forEach(f -> {
                    try {
                        IStat<?> stat = StatTracker.getStat(player, (Stats)f.get(null));
                        msg.add(String.format("%s: %f", f.getName(), stat.getValue().doubleValue()));
                    } catch (IllegalAccessException ignored) { }
                });
        sender.sendMessage(msg.toArray(new String[msg.size()]));
    }

    private static void cmdItems(CommandSender sender) {
        sender.sendMessage("Known items:");
        sender.sendMessage(ItemRegistry.stream()
                .map(i -> String.format("%s:%d -> %s", i.getType().material, i.getType().meta, i.getId()))
                .toArray(String[]::new));
    }

    private static void cmdMutate(CommandSender sender, String[] args) {
        if (playerCheckFailed(sender) || argCountCheckFailed(sender, args, 2))
            return;
        IStat<Integer> stat;
        if (args[0].equalsIgnoreCase("mana"))
            stat = StatTracker.getStat((Player)sender, Stats.MANA);
        else if (args[0].equalsIgnoreCase("hp"))
            stat = StatTracker.getStat((Player)sender, Stats.HP);
        else
            throw new IllegalStateException("what");
        stat.setValue(Integer.parseInt(args[1]));
    }

    private static void msgHelpNoArgs(CommandSender r) {
        r.sendMessage("/mfrp <stats|items|hp|mana>");
    }

    private static boolean playerCheckFailed(CommandSender sender) {
        if (sender instanceof Player)
            return false;
        sender.sendMessage("Only players can use this command.");
        return true;
    }

    private static boolean argCountCheckFailed(CommandSender sender, String[] args, int count) {
        if (args.length >= count)
            return false;
        sender.sendMessage("Not enough arguments.");
        return true;
    }

    private static boolean permCheckFailed(CommandSender sender) {
        if (sender.hasPermission("mfrp.admin"))
            return false;
        sender.sendMessage("No permission.");
        return true;
    }

}
