package io.github.phantamanta44.mobafort.mfrp.item;

import io.github.phantamanta44.mobafort.lib.item.ItemSig;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ItemRegistry {

    private static Map<String, IItem> itemMap;

    public static void init() {
        itemMap = new HashMap<>();
    }

    public static void register(IItem item) {
        itemMap.put(item.getId(), item);
    }

    public static IItem get(ItemStack stack) {
        return itemMap.values().stream()
                .filter(e -> e.getType().matches(stack))
                .findAny()
                .orElse(null);
    }

    public static IItem get(ItemSig sig) {
        return itemMap.values().stream()
                .filter(e -> e.getType().equals(sig))
                .findAny()
                .orElse(null);
    }

    public static IItem get(String id) {
        return itemMap.get(id);
    }

    public static Stream<IItem> stream() {
        return itemMap.values().stream();
    }

}
