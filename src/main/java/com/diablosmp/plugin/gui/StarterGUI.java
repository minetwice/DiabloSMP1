package com.diablosmp.plugin.gui;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StarterGUI implements Listener {
    private static final String STARTER_TITLE = "<gradient:#FF5555:#AA0000>Starter Diablo Stone</gradient>";
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void openStarterMenu(DiabloSMP plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, miniMessage.deserialize(STARTER_TITLE));

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        if (fMeta != null) {
            fMeta.displayName(Component.empty());
            filler.setItemMeta(fMeta);
        }
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        DiabloStoneType[] values = DiabloStoneType.values();
        for (int i = 0; i < values.length && i < 18; i++) {
            DiabloStoneType type = values[i];
            StoneConfig config = plugin.getConfigManager().getStoneConfig(type);

            ItemStack item = new ItemStack(config != null ? config.getMaterial() : type.getFallbackMaterial());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(miniMessage.deserialize(config != null ? config.getDisplayName() : type.getDefaultDisplayName()));

                List<Component> lore = new ArrayList<>();
                if (config != null && config.getDescription() != null) {
                    for (String l : config.getDescription()) {
                        lore.add(miniMessage.deserialize(l));
                    }
                }
                lore.add(Component.empty());
                lore.add(miniMessage.deserialize("<green>Click to select as your starter Diablo Stone!</green>"));
                meta.lore(lore);

                if (config != null) {
                    meta.setCustomModelData(config.getCustomModelData());
                }
                item.setItemMeta(meta);
            }

            inv.setItem(i < 9 ? i : (i + 9), item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().title().equals(miniMessage.deserialize(STARTER_TITLE))) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0 || rawSlot >= 27) return;

        DiabloSMP plugin = DiabloSMP.getInstance();
        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());

        if (data.isFirstJoinClaimed()) {
            player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("first-join-already-claimed")));
            player.closeInventory();
            return;
        }

        DiabloStoneType[] values = DiabloStoneType.values();
        int stoneIdx = -1;
        if (rawSlot < 9) stoneIdx = rawSlot;
        else if (rawSlot >= 9 && rawSlot < 18) stoneIdx = rawSlot - 9 + 9;

        if (stoneIdx >= 0 && stoneIdx < values.length) {
            DiabloStoneType selected = values[stoneIdx];
            data.addStone(selected);
            data.setActiveStone(selected);
            data.setFirstJoinClaimed(true);

            plugin.getStorageService().savePlayerData(player.getUniqueId(), true);
            plugin.getHudService().updateHud(player);

            player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("first-join-claimed").replace("{stone}", selected.name())));
            player.closeInventory();
        }
    }
}
