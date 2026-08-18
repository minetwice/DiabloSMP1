package com.twicefear.diablosmp.listener;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.ability.AbilityRegistry;
import com.twicefear.diablosmp.util.AbilityItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityClickListener implements Listener {

    private final DiabloSMP plugin;

    public AbilityClickListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!AbilityItem.isAbilityItem(item)) {
            return;
        }

        String abilityId = AbilityItem.getAbilityId(item);
        if (abilityId == null) return;

        Ability ability = plugin.getAbilityRegistry().getAbility(abilityId);
        if (ability == null) return;

        event.setCancelled(true);

        if (!player.hasPermission("diablosmp.ability.use")) {
            player.sendMessage(plugin.prefix() + ChatColor.RED + "You don't have permission to use abilities.");
            return;
        }

        AbilityRegistry registry = plugin.getAbilityRegistry();

        if (registry.isOnCooldown(player)) {
            int remaining = registry.getRemainingCooldown(player);
            player.sendMessage(plugin.prefix() + ChatColor.RED
                    + "This ability is on cooldown for " + ChatColor.YELLOW + remaining + "s"
                    + ChatColor.RED + "!");
            return;
        }

        if (registry.hasActiveAbility(player)) {
            player.sendMessage(plugin.prefix() + ChatColor.RED + "You already have an active ability!");
            return;
        }

        registry.setCooldown(player, ability.getCooldownSeconds());
        registry.setActive(player, ability.getId());
        ability.onCast(player);

        int clearDelay = Math.max(ability.getCooldownSeconds(), 5) * 20;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            registry.clearActive(player);
        }, clearDelay);
    }
}
