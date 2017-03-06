package io.github.phantamanta44.mobafort.mfrp.resource;

import io.github.phantamanta44.mobafort.lib.math.MathUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourceTracker {

    private static Map<UUID, ResourceInfo> resMap;

    public static void init() {
        resMap = new HashMap<>();
    }

    public static int getHp(Player player) {
        return getOrCreateEntry(player.getUniqueId()).hp;
    }

    public static int getMana(Player player) {
        return getOrCreateEntry(player.getUniqueId()).mana;
    }

    public static void setHp(Player player, int amt) {
        getOrCreateEntry(player.getUniqueId()).hp = Math.max(amt, 0);
    }

    public static void setMana(Player player, int amt) {
        getOrCreateEntry(player.getUniqueId()).mana = Math.max(amt, 0);
    }

    public static void setHp(Player player, int amt, int cap) {
        getOrCreateEntry(player.getUniqueId()).hp = MathUtils.clamp(amt, 0, cap);
    }

    public static void setMana(Player player, int amt, int cap) {
        getOrCreateEntry(player.getUniqueId()).mana = MathUtils.clamp(amt, 0, cap);
    }

    public static void addHp(Player player, int amt) {
        ResourceInfo ri = getOrCreateEntry(player.getUniqueId());
        ri.hp = Math.max(ri.hp + amt, 0);
    }

    public static void addMana(Player player, int amt) {
        ResourceInfo ri = getOrCreateEntry(player.getUniqueId());
        ri.mana = Math.max(ri.mana + amt, 0);
    }

    public static void addHp(Player player, int amt, int cap) {
        ResourceInfo ri = getOrCreateEntry(player.getUniqueId());
        ri.hp = MathUtils.clamp(ri.hp + amt, 0, cap);
    }

    public static void addMana(Player player, int amt, int cap) {
        ResourceInfo ri = getOrCreateEntry(player.getUniqueId());
        ri.mana = MathUtils.clamp(ri.mana + amt, 0, cap);
    }

    public static void capResources(Player player, int hpMax, int manaMax) {
        ResourceInfo ri = getOrCreateEntry(player.getUniqueId());
        ri.hp = MathUtils.clamp(ri.hp, 0, hpMax);
        ri.mana = MathUtils.clamp(ri.mana, 0, manaMax);
    }

    public static ResourceInfo getInfo(Player player) {
        return getOrCreateEntry(player.getUniqueId());
    }

    public static void uncache(Player player) {
        resMap.remove(player.getUniqueId());
    }

    private static ResourceInfo getOrCreateEntry(UUID key) {
        ResourceInfo info = resMap.computeIfAbsent(key, k -> new ResourceInfo());
        return info;
    }

    public static class ResourceInfo {

        private int hp, mana;

        public ResourceInfo() {
            this(0, 0);
        }

        public ResourceInfo(int hp, int mana) {
            this.hp = hp;
            this.mana = mana;
        }

        public int getHp() {
            return hp;
        }

        public int getMana() {
            return mana;
        }

        public void setHp(int hp) {
            this.hp = hp;
        }

        public void setMana(int mana) {
            this.mana = mana;
        }

    }

}
