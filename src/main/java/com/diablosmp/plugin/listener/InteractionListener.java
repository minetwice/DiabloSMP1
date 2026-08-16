package com.diablosmp.plugin.listener;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.gui.DiabloMenuGUI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class InteractionListener implements Listener {
    private final DiabloSMP plugin;

    public InteractionListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("casting.right-click-enabled", true)) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        // Sneak + Right Click to open menu
        if (player.isSneaking() && plugin.getConfig().getBoolean("casting.sneak-right-click-open-menu", true)) {
            event.setCancelled(true);
            DiabloMenuGUI.openMenu(plugin, player);
            return;
        }

        // Check blocked block interactions (containers, doors, buttons)
        if (action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            List<String> blocked = plugin.getConfig().getStringList("casting.blocked-materials");
            if (blocked.contains(block.getType().name())) {
                return; // Allow container/button opening without casting
            }
        }

        boolean itemMode = plugin.getConfig().getBoolean("stones.item-mode", false);
        boolean allowEmptyHand = plugin.getConfig().getBoolean("casting.allow-empty-hand", true);

        if (!itemMode && !allowEmptyHand && player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            return;
        }

        boolean castTriggered = plugin.getCastingService().tryCast(player);
        if (castTriggered) {
            event.setCancelled(true);
        }
    }
}
