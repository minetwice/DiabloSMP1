package com.twicefear.diablosmp.listeners;

import com.twicefear.diablosmp.DiabloSMP;
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

        plugin.getAbsorbManager().incrementShift(player);

        if (plugin.getAbsorbManager().getShiftCount(player) >= plugin.getConfigManager().getShiftsRequired()) {
            StoneType type = plugin.getStoneManager().getStoneType(hand);
            if (type == null) return;

            // Remove item
            hand.setAmount(hand.getAmount() - 1);

            // Absorb
            plugin.getAbsorbManager().setAbsorbed(player, type);
            plugin.getAbsorbManager().playAbsorbAnimation(player, type);
        }
    }
}
