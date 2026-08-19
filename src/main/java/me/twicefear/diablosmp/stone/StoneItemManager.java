package me.twicefear.diablosmp.stone;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import me.twicefear.diablosmp.DiabloSMP;

import java.util.ArrayList;
import java.util.List;

public class StoneItemManager {

    private final DiabloSMP plugin;
    private final NamespacedKey stoneKey;

    public StoneItemManager(DiabloSMP plugin) {
        this.plugin = plugin;
        this.stoneKey = new NamespacedKey(plugin, "diablo_stone_type");
    }

    public ItemStack createStoneItem(StoneType stoneType) {
        ItemStack item = new ItemStack(Material.NETHERITE_SCRAP);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(stoneType.getDisplayName());
            meta.setCustomModelData(stoneType.getCustomModelData());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "A legendary Diablo Stone pulsing with ancient power.");
            lore.add("");
            lore.add(ChatColor.GOLD + "Primary Ability (Right Click):");
            lore.add(ChatColor.YELLOW + "  " + stoneType.getPrimaryAbilityName() + ChatColor.DARK_GRAY + " (" + stoneType.getPrimaryCooldown() + "s CD)");
            lore.add(ChatColor.GOLD + "Secondary Ability (Shift + Right Click):");
            lore.add(ChatColor.YELLOW + "  " + stoneType.getSecondaryAbilityName() + ChatColor.DARK_GRAY + " (" + stoneType.getSecondaryCooldown() + "s CD)");
            lore.add("");
            lore.add(ChatColor.DARK_PURPLE + "Crouch 3x while holding to absorb into body!");

            meta.setLore(lore);
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(stoneKey, PersistentDataType.STRING, stoneType.getId());

            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isDiabloStone(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(stoneKey, PersistentDataType.STRING);
    }

    public StoneType getStoneType(ItemStack item) {
        if (!isDiabloStone(item)) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String id = pdc.get(stoneKey, PersistentDataType.STRING);
        return id != null ? StoneType.fromId(id) : null;
    }

    public NamespacedKey getStoneKey() {
        return stoneKey;
    }
}
