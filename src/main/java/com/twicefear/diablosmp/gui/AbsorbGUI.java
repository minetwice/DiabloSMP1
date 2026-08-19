package com.twicefear.diablosmp.gui;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stones.StoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AbsorbGUI {

    private final DiabloSMP plugin;
    private final Player player;
    private final StoneType type;

    public AbsorbGUI(DiabloSMP plugin, Player player, StoneType type) {
        this.plugin = plugin;
        this.player = player;
        this.type = type;
    }

    public void open() {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Absorb Diablo Stone")
                .color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD));

        // Center slot for stone
        inv.setItem(4, plugin.getStoneManager().createStoneItem(type));

        // Info items
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta meta = info.getItemMeta();
        meta.displayName(Component.text("§ePlace the stone in the center"));
        meta.lore(java.util.List.of(
                Component.text("§7to absorb it into your body."),
                Component.text("§cThis cannot be undone easily.")
        ));
        info.setItemMeta(meta);
        inv.setItem(0, info);
        inv.setItem(8, info);

        player.openInventory(inv);
    }
}
