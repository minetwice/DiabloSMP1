package com.twicefear.diablosmp.util;

import com.twicefear.diablosmp.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class AbilityItem {

    private static final org.bukkit.NamespacedKey ABILITY_KEY =
            new org.bukkit.NamespacedKey("diablosmp", "ability_id");

    public static ItemStack create(Ability ability) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ChatColor.GOLD + ability.getDisplayName()
                + ChatColor.GRAY + " [" + ability.getElement() + "]");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + ability.getDescription());
        lore.add("");
        lore.add(ChatColor.YELLOW + "Right-Click" + ChatColor.GRAY + " to cast");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ability.getCooldownSeconds() + "s");
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(ABILITY_KEY, PersistentDataType.STRING, ability.getId());
        meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isAbilityItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(ABILITY_KEY, PersistentDataType.STRING);
    }

    public static String getAbilityId(ItemStack item) {
        if (!isAbilityItem(item)) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(ABILITY_KEY, PersistentDataType.STRING);
    }
}
