package com.sdmine.elementalcore.gui;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.items.ItemFactory;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SocketGUIListener implements org.bukkit.event.Listener {
    private final ElementalCorePlugin plugin;
    private final Map<UUID, SocketGUI> openGUIs;

    public SocketGUIListener(ElementalCorePlugin plugin) { this.plugin = plugin; this.openGUIs = new HashMap<>(); }

    public void openGUI(Player player, ItemStack blade) {
        SocketGUI gui = new SocketGUI(plugin, player, blade);
        openGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof SocketGUI)) return;
        SocketGUI gui = (SocketGUI) top.getHolder();
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(top)) return;
        int slot = event.getRawSlot();
        if (gui.isResultSlot(slot)) return;
        if (gui.isSocketSlot(slot)) handleSocket(event, gui, slot);
    }

    private void handleSocket(InventoryClickEvent event, SocketGUI gui, int slot) {
        Player p = (Player) event.getWhoClicked();
        Inventory top = event.getView().getTopInventory();
        ItemStack cursor = event.getCursor(), current = top.getItem(slot);
        ItemFactory f = plugin.getItemFactory();
        if (current != null && !current.getType().isAir()) {
            if (cursor == null || cursor.getType().isAir()) { event.setCursor(current); top.setItem(slot, null); playSound(p, "socket_remove"); gui.updateResultPreview(); }
            return;
        }
        if (current == null || current.getType().isAir()) {
            if (cursor != null && f.isElementalCore(cursor)) {
                ItemStack place = cursor.clone(); place.setAmount(1); top.setItem(slot, place);
                if (cursor.getAmount() > 1) { cursor.setAmount(cursor.getAmount()-1); event.setCursor(cursor); } else event.setCursor(null);
                playSound(p, "socket_insert"); gui.updateResultPreview();
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) { if (event.getView().getTopInventory().getHolder() instanceof SocketGUI) event.setCancelled(true); }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof SocketGUI)) return;
        SocketGUI gui = (SocketGUI) event.getInventory().getHolder();
        Player p = (Player) event.getPlayer();
        gui.returnItems(); gui.applyChanges();
        ItemStack blade = gui.getBlade();
        if (plugin.getItemFactory().isElementalBlade(p.getInventory().getItemInMainHand()))
            p.getInventory().setItemInMainHand(blade);
        openGUIs.remove(p.getUniqueId());
    }

    private void playSound(Player p, String key) {
        String s = plugin.getConfig().getString("sounds." + key + ".sound", "");
        if (s.isEmpty()) return;
        try { p.playSound(p.getLocation(), Sound.valueOf(s), (float)plugin.getConfig().getDouble("sounds."+key+".volume",1.0), (float)plugin.getConfig().getDouble("sounds."+key+".pitch",1.0)); }
        catch (IllegalArgumentException ignored) {}
    }
}
