package com.twicefear.diablosmp.listener;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.command.DSMPCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StartGUIListener implements Listener {

    private final DiabloSMP plugin;
    private final DSMPCommand command;

    public StartGUIListener(DiabloSMP plugin, DSMPCommand command) {
        this.plugin = plugin;
        this.command = command;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle() == null) return;
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (!command.isStartGUI(title)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();
        boolean rightClick = event.isRightClick();
        command.handleGUIClick(player, slot, rightClick);
    }
}
