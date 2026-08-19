package com.twicefear.diablosmp.listeners;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class AbilityListener implements Listener {

    private final DiabloSMP plugin;

    public AbilityListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        if (!plugin.getSmpManager().isRunning() && !player.hasPermission("diablosmp.admin")) {
            player.sendMessage(plugin.getConfigManager().getMessage("smp-not-started"));
            return;
        }

        if (!plugin.getAbsorbManager().hasAbsorbed(player)) return;

        StoneType type = plugin.getAbsorbManager().getAbsorbed(player);
        boolean primary = !player.isSneaking();

        if (plugin.getCooldownManager().isOnCooldown(player, type, primary)) {
            long rem = plugin.getCooldownManager().getRemaining(player, type, primary) / 1000;
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cCooldown: " + rem + "s"));
            return;
        }

        // Execute ability
        plugin.getAbilityManager().execute(player, type, primary);
        plugin.getCooldownManager().startCooldown(player, type, primary);
    }
}
