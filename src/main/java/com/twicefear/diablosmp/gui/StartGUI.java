package com.twicefear.diablosmp.gui;

import com.twicefear.diablosmp.DiabloSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class StartGUI {

    private final DiabloSMP plugin;
    private final Player player;
    private int minutes;
    private int seconds;

    public StartGUI(DiabloSMP plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.minutes = plugin.getConfigManager().getGracePeriodDefault() / 60;
        this.seconds = plugin.getConfigManager().getGracePeriodDefault() % 60;
    }

    public void open() {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("DiabloSMP - Start Settings")
                .color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));

        // Minutes display
        inv.setItem(11, createItem(Material.CLOCK, "§eMinutes: §f" + minutes,
                List.of("§7Left Click: +1", "§7Right Click: -1", "§7Shift: +5/-5")));

        // Seconds display
        inv.setItem(15, createItem(Material.COMPASS, "§eSeconds: §f" + seconds,
                List.of("§7Left Click: +5", "§7Right Click: -5")));

        // Confirm
        inv.setItem(22, createItem(Material.LIME_CONCRETE, "§a§lSTART SMP",
                List.of("§7Grace Period: §f" + minutes + "m " + seconds + "s",
                        "§aClick to start the SMP!")));

        // Cancel
        inv.setItem(26, createItem(Material.RED_CONCRETE, "§cCancel", List.of("§7Close menu")));

        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name.replace('&', '§')));
        meta.lore(lore.stream().map(s -> Component.text(s.replace('&', '§'))).toList());
        item.setItemMeta(meta);
        return item;
    }

    public void handleClick(int slot, boolean left, boolean shift) {
        if (slot == 11) { // Minutes
            int change = shift ? 5 : 1;
            if (left) minutes = Math.min(60, minutes + change);
            else minutes = Math.max(0, minutes - change);
            open(); // refresh
        } else if (slot == 15) { // Seconds
            if (left) seconds = Math.min(55, seconds + 5);
            else seconds = Math.max(0, seconds - 5);
            open();
        } else if (slot == 22) { // Start
            int total = minutes * 60 + seconds;
            if (total < 10) total = 10;
            player.closeInventory();
            plugin.getSmpManager().startSMP(total);
            player.sendMessage("§aSMP started with grace period: " + minutes + "m " + seconds + "s");
        } else if (slot == 26) {
            player.closeInventory();
        }
    }
}
