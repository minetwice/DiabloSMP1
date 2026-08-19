package com.twicefear.diablosmp.listeners;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.gui.AbsorbGUI;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (!plugin.getStoneManager().isDiabloStone(hand)) return;
        if (plugin.getAbsorbManager().hasAbsorbed(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("already-absorbed"));
            return;
        }

        // Increment and get new count
        plugin.getAbsorbManager().incrementShift(player);
        int current = plugin.getAbsorbManager().getShiftCount(player);

        // Open GUI when we just reached the required shifts
        if (current >= plugin.getConfigManager().getShiftsRequired()) {
            StoneType type = plugin.getStoneManager().getStoneType(hand);
            if (type == null) return;

            // Reset count and open GUI
            plugin.getAbsorbManager().resetShift(player);
            new AbsorbGUI(plugin, player, type).open();
        }
    }
}
