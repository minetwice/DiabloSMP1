package com.twicefear.diablosmp.listener;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ProtectionListener implements Listener {

    private final DiabloSMP plugin;

    public ProtectionListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.config().allowDrop() && plugin.stones().isStone(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.messages().prefixed("no-permission")
                    .replace("don't have permission", "stones cannot be dropped"));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (plugin.config().allowStore()) return;
        if (event.getInventory().getType() == InventoryType.PLAYER) return;
        if (event.getInventory().getType() == InventoryType.CRAFTING) return;
        if (event.getInventory().getType() == InventoryType.CREATIVE) return;

        // Exempt the absorb GUI so stones can be placed into it
        if (event.getView().getTitle() != null) {
            String title = ChatColor.stripColor(event.getView().getTitle());
            if (title.equals("Place Stone to Absorb")) return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) item = event.getCursor();
        if (plugin.stones().isStone(item)) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player p) {
                p.sendMessage(plugin.messages().prefixed("no-permission")
                        .replace("don't have permission", "stones cannot be stored"));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (plugin.config().keepOnDeath()) {
            event.getDrops().removeIf(plugin.stones()::isStone);
        }
    }
}
