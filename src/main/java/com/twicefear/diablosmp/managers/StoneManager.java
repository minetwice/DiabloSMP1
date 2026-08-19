package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stones.StoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class StoneManager {

    private final DiabloSMP plugin;
    private final NamespacedKey stoneKey;

    public StoneManager(DiabloSMP plugin) {
        this.plugin = plugin;
        this.stoneKey = new NamespacedKey(plugin, "diablo_stone");
    }

    public void loadStones() {
        // All stones are defined in enum - ready
        plugin.getLogger().info("Loaded " + StoneType.values().length + " Diablo Stones");
    }

    public ItemStack createStoneItem(StoneType type) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(type.getDisplayName())
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§8§m------------------"));
        lore.add(Component.text("§7A legendary Diablo Stone"));
        lore.add(Component.text("§7Hold and shift 3 times to absorb"));
        lore.add(Component.empty());
        lore.add(Component.text("§ePrimary: §fRight Click"));
        lore.add(Component.text("§eSecondary: §fShift + Right Click"));
        lore.add(Component.text("§8§m------------------"));
        meta.lore(lore);

        meta.getPersistentDataContainer().set(stoneKey, PersistentDataType.STRING, type.name());
        meta.setCustomModelData(1000 + type.ordinal()); // for resource pack models

        item.setItemMeta(meta);
        return item;
    }

    public boolean isDiabloStone(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(stoneKey, PersistentDataType.STRING);
    }

    public StoneType getStoneType(ItemStack item) {
        if (!isDiabloStone(item)) return null;
        String name = item.getItemMeta().getPersistentDataContainer().get(stoneKey, PersistentDataType.STRING);
        try {
            return StoneType.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }

    public NamespacedKey getStoneKey() {
        return stoneKey;
    }
}
