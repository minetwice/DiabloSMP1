package com.twicefear.diablosmp.listeners;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.gui.StartGUI;
import com.twicefear.diablosmp.stones.StoneType;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private final DiabloSMP plugin;
    private final Map<UUID, StartGUI> openStartGUIs = new HashMap<>();

    public GUIListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void registerStartGUI(Player player, StartGUI gui) {
        openStartGUIs.put(player.getUniqueId(), gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (title.contains("Start Settings")) {
            event.setCancelled(true);
            StartGUI gui = openStartGUIs.get(player.getUniqueId());
            if (gui != null) {
                gui.handleClick(event.getSlot(), event.isLeftClick(), event.isShiftClick());
            }
            return;
        }

        if (title.contains("Absorb Diablo Stone")) {
            event.setCancelled(true);

            if (event.getSlot() == 4) {
                ItemStack current = event.getCurrentItem();
                if (current != null && plugin.getStoneManager().isDiabloStone(current)) {
                    StoneType type = plugin.getStoneManager().getStoneType(current);
                    if (type != null && !plugin.getAbsorbManager().hasAbsorbed(player)) {
                        // Remove from inventory
                        player.getInventory().removeItem(current);
                        plugin.getAbsorbManager().setAbsorbed(player, type);
                        plugin.getAbsorbManager().playAbsorbAnimation(player, type);
                        player.closeInventory();
                    }
                }
            }
        }
    }
}
