package com.sdmine.elementalcore.gui;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
import com.sdmine.elementalcore.items.ItemFactory;
import com.sdmine.elementalcore.socket.SocketManager;
import com.sdmine.elementalcore.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class SocketGUI implements InventoryHolder {
    private final ElementalCorePlugin plugin;
    private final Inventory inventory;
    private final Player player;
    private final ItemStack blade;
    private final int[] socketSlots;
    private final int resultSlot;

    public SocketGUI(ElementalCorePlugin plugin, Player player, ItemStack blade) {
        this.plugin = plugin; this.player = player; this.blade = blade;
        List<Integer> slots = plugin.getConfig().getIntegerList("gui.socket_slots");
        if (slots.size() < 3) slots = List.of(4, 5, 6);
        this.socketSlots = new int[]{slots.get(0), slots.get(1), slots.get(2)};
        this.resultSlot = plugin.getConfig().getInt("gui.result_slot", 2);
        String title = MessageUtil.color(plugin.getConfig().getString("gui.title", "&8&lElemental Socket Menu"));
        this.inventory = Bukkit.createInventory(this, 9, title);
        populate();
    }

    private void populate() {
        ItemFactory factory = plugin.getItemFactory(); SocketManager sm = plugin.getSocketManager();
        String fm = plugin.getConfig().getString("gui.filler_material", "GRAY_STAINED_GLASS_PANE");
        Material fMat = Material.matchMaterial(fm); if (fMat == null) fMat = Material.GRAY_STAINED_GLASS_PANE;
        String fName = MessageUtil.color(plugin.getConfig().getString("gui.filler_display_name", "&7"));
        for (int i = 0; i < 9; i++) { if (i != resultSlot && !isSocketSlot(i)) { ItemStack pane = new ItemStack(fMat); ItemMeta m = pane.getItemMeta(); if (m != null) { m.setDisplayName(fName); pane.setItemMeta(m); } inventory.setItem(i, pane); } }
        CoreType[] cur = sm.readSockets(blade);
        for (int i = 0; i < 3; i++) if (cur[i] != null) inventory.setItem(socketSlots[i], factory.createCore(cur[i]));
        updateResultPreview();
    }

    public void updateResultPreview() {
        ItemFactory factory = plugin.getItemFactory(); SocketManager sm = plugin.getSocketManager();
        ItemStack copy = blade.clone(); ItemMeta meta = copy.getItemMeta(); if (meta == null) return;
        CoreType[] ns = new CoreType[3];
        for (int i = 0; i < 3; i++) { ItemStack si = inventory.getItem(socketSlots[i]); if (si != null && factory.isElementalCore(si)) ns[i] = factory.getCoreType(si); }
        sm.writeSockets(meta, ns); copy.setItemMeta(meta); factory.updateBladeDisplay(copy);
        inventory.setItem(resultSlot, copy);
    }

    public void applyChanges() {
        ItemFactory factory = plugin.getItemFactory(); SocketManager sm = plugin.getSocketManager();
        CoreType[] ns = new CoreType[3];
        for (int i = 0; i < 3; i++) { ItemStack si = inventory.getItem(socketSlots[i]); if (si != null && factory.isElementalCore(si)) ns[i] = factory.getCoreType(si); }
        ItemMeta meta = blade.getItemMeta(); if (meta != null) { sm.writeSockets(meta, ns); blade.setItemMeta(meta); factory.updateBladeDisplay(blade); }
    }

    public void returnItems() {
        for (int i = 0; i < 3; i++) { ItemStack si = inventory.getItem(socketSlots[i]); if (si != null && !si.getType().isAir()) { player.getInventory().addItem(si); inventory.setItem(socketSlots[i], null); } }
    }

    public boolean isSocketSlot(int s) { for (int v : socketSlots) if (v == s) return true; return false; }
    public boolean isResultSlot(int s) { return s == resultSlot; }
    public int[] getSocketSlots() { return socketSlots; }
    public int getResultSlot() { return resultSlot; }
    public ItemStack getBlade() { return blade; }
    @Override public Inventory getInventory() { return inventory; }
}
