package com.twicefear.diablosmp.listener;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AbsorbListener implements Listener {

    private final DiabloSMP plugin;

    public AbsorbListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (plugin.smp().isIdle()) return;
        if (plugin.players().hasAbsorbed(player.getUniqueId())) return;
        ItemStack held = player.getInventory().getItemInMainHand();
        if (plugin.stones().getStoneType(held) == null) return;
        if (plugin.stones().isAbsorbed(held)) return;
        plugin.absorb().onShift(player, held);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.getView().getTitle() == null) return;
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (!title.equals("Place Stone to Absorb")) return;
        if (event.getRawSlot() != 4) { event.setCancelled(true); return; }
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            event.setCancelled(true); return;
        }
        ItemStack cursor = event.getCursor();
        StoneType type = plugin.stones().getStoneType(cursor);
        if (type == null) { event.setCancelled(true); return; }
        event.setCancelled(true);
        event.setCursor(null);
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.absorb().absorb((Player) event.getWhoClicked(), type);
        });
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        if (event.getView().getTitle() == null) return;
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (!title.equals("Place Stone to Absorb")) return;
        Inventory inv = event.getInventory();
        ItemStack item = inv.getItem(4);
        if (item != null && plugin.stones().isStone(item)) {
            if (event.getPlayer() instanceof Player p) {
                p.getInventory().addItem(item);
            }
        }
    }
}
