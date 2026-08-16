package com.diablosmp.plugin.listener;

import com.diablosmp.plugin.DiabloSMP;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class SoulboundListener implements Listener {
    private final DiabloSMP plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public SoulboundListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!plugin.getConfig().getBoolean("stones.soulbound", true)) return;
        if (!plugin.getConfig().getBoolean("stones.prevent-drop", true)) return;

        ItemStack item = event.getItemDrop().getItemStack();
        if (plugin.getCastingService().isSoulbound(item)) {
            if (!event.getPlayer().hasPermission("diablosmp.admin")) {
                event.setCancelled(true);
                sendMsg(event.getPlayer(), "soulbound-deny-drop");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.getConfig().getBoolean("stones.soulbound", true)) return;
        if (!plugin.getConfig().getBoolean("stones.prevent-container", true)) return;

        if (event.getWhoClicked() instanceof Player player) {
            if (player.hasPermission("diablosmp.admin")) return;

            ItemStack current = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

            if (plugin.getCastingService().isSoulbound(current) || plugin.getCastingService().isSoulbound(cursor)) {
                if (event.getClickedInventory() != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                    sendMsg(player, "soulbound-deny-container");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!plugin.getConfig().getBoolean("stones.soulbound", true)) return;
        if (!plugin.getConfig().getBoolean("stones.prevent-container", true)) return;

        if (event.getWhoClicked() instanceof Player player) {
            if (player.hasPermission("diablosmp.admin")) return;

            if (plugin.getCastingService().isSoulbound(event.getOldCursor())) {
                if (event.getInventory().getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                    sendMsg(player, "soulbound-deny-container");
                }
            }
        }
    }

    private void sendMsg(Player player, String key) {
        String msg = plugin.getConfigManager().getMessage(key);
        player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() + msg));
    }
}
