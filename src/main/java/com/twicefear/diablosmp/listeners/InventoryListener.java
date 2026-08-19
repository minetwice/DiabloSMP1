package com.twicefear.diablosmp.listeners;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryListener implements Listener {

    private final DiabloSMP plugin;

    public InventoryListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.getConfigManager().canStonesBeStored()) {
            if (event.getCurrentItem() != null && plugin.getStoneManager().isDiabloStone(event.getCurrentItem())) {
                if (event.getInventory().getType() != InventoryType.PLAYER && event.getInventory().getType() != InventoryType.CRAFTING) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage("§cDiablo Stones cannot be stored in containers!");
                }
            }
        }
    }
}
