package com.twicefear.diablosmp.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class DiabloStone {
    
    private final StoneType type;
    private final int primaryCooldown; // seconds
    private final int secondaryCooldown; // seconds
    private boolean absorbed;
    
    public DiabloStone(StoneType type, int primaryCooldown, int secondaryCooldown) {
        this.type = type;
        this.primaryCooldown = primaryCooldown * 1000; // convert to ms
        this.secondaryCooldown = secondaryCooldown * 1000;
        this.absorbed = false;
    }
    
    public StoneType getType() {
        return type;
    }
    
    public int getPrimaryCooldown() {
        return primaryCooldown;
    }
    
    public int getSecondaryCooldown() {
        return secondaryCooldown;
    }
    
    public boolean isAbsorbed() {
        return absorbed;
    }
    
    public void setAbsorbed(boolean absorbed) {
        this.absorbed = absorbed;
    }
    
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(type.getColoredName());
            List<String> lore = Arrays.asList(
                "§7Right Click: §ePrimary Ability",
                "§7Shift+Right Click: §eSecondary Ability",
                "",
                "§8Shift 3 times to absorb",
                "§8/ diablostone withdraw to remove",
                "",
                "§6§lDiablo Stone"
            );
            meta.setLore(lore);
            
            // Set custom model data for resource pack
            meta.setCustomModelData(type.ordinal() + 1000);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    @Override
    public String toString() {
        return "DiabloStone{" +
                "type=" + type +
                ", absorbed=" + absorbed +
                '}';
    }
}
