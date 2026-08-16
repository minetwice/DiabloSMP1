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

public class DiabloMenuGUI implements Listener {
    private static final String GUI_TITLE = "<gradient:#FF5555:#AA0000>Diablo Stones</gradient>";
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void openMenu(DiabloSMP plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, miniMessage.deserialize(GUI_TITLE));

        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
        DiabloStoneType active = data.getActiveStone();

        // Fill background gray panes
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        if (fMeta != null) {
            fMeta.displayName(Component.empty());
            filler.setItemMeta(fMeta);
        }
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        // Place 15 Diablo Stones across slots 0 to 14
        DiabloStoneType[] values = DiabloStoneType.values();
        for (int i = 0; i < values.length && i < 18; i++) {
            DiabloStoneType type = values[i];
            StoneConfig config = plugin.getConfigManager().getStoneConfig(type);

            boolean owned = data.hasStone(type);
            boolean isActive = (active == type);

            ItemStack item = new ItemStack(owned ? (config != null ? config.getMaterial() : type.getFallbackMaterial()) : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String nameStr = config != null ? config.getDisplayName() : type.getDefaultDisplayName();
                if (isActive) {
                    nameStr += " <gold>★ (Active)</gold>";
                } else if (!owned) {
                    nameStr = "<gray>" + type.name() + " [Locked]</gray>";
                }
                meta.displayName(miniMessage.deserialize(nameStr));

                List<Component> lore = new ArrayList<>();
                if (config != null && config.getDescription() != null) {
                    for (String l : config.getDescription()) {
                        lore.add(miniMessage.deserialize(l));
                    }
                }
                lore.add(Component.empty());
                lore.add(miniMessage.deserialize("<gray>Cooldown: <white>" + (config != null ? config.getCooldownSeconds() : 20.0) + "s</white></gray>"));
                lore.add(miniMessage.deserialize("<gray>Damage: <white>" + (config != null ? config.getDirectDamage() : 10.0) + "</white></gray>"));
                lore.add(miniMessage.deserialize("<gray>Radius: <white>" + (config != null ? config.getRadius() : 10.0) + "</white></gray>"));
                lore.add(Component.empty());

                if (isActive) {
                    lore.add(miniMessage.deserialize("<green>Currently Selected Active Stone</green>"));
                } else if (owned) {
                    lore.add(miniMessage.deserialize("<yellow>Click to set as Active Stone</yellow>"));
                } else {
                    lore.add(miniMessage.deserialize("<red>Locked (Not Owned)</red>"));
                }

                meta.lore(lore);
                if (config != null && owned) {
                    meta.setCustomModelData(config.getCustomModelData());
                }
                item.setItemMeta(meta);
            }

            int slot = (i < 9) ? i : (i + 18 - 9); // neat distribution
            inv.setItem(i < 9 ? i : (i + 9), item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().title().equals(miniMessage.deserialize(GUI_TITLE))) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0 || rawSlot >= 27) return;

        DiabloSMP plugin = DiabloSMP.getInstance();
        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());

        DiabloStoneType[] values = DiabloStoneType.values();
        int stoneIdx = -1;
        if (rawSlot < 9) stoneIdx = rawSlot;
        else if (rawSlot >= 9 && rawSlot < 18) stoneIdx = rawSlot - 9 + 9;

        if (stoneIdx >= 0 && stoneIdx < values.length) {
            DiabloStoneType clickedStone = values[stoneIdx];
            if (data.hasStone(clickedStone)) {
                data.setActiveStone(clickedStone);
                plugin.getStorageService().savePlayerData(player.getUniqueId(), true);
                plugin.getHudService().updateHud(player);
                player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() +
                        plugin.getConfigManager().getMessage("active-stone-set").replace("{stone}", clickedStone.name())));
                openMenu(plugin, player); // refresh menu
            } else {
                player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() +
                        plugin.getConfigManager().getMessage("stone-not-owned").replace("{stone}", clickedStone.name())));
            }
        }
    }
}
