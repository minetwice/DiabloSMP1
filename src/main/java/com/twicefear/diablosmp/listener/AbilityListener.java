package com.twicefear.diablosmp.listener;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.stone.StoneType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityListener implements Listener {

    private final DiabloSMP plugin;

    public AbilityListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (plugin.smp().isIdle()) return;
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        StoneType stone = plugin.players().getAbsorbedStone(player.getUniqueId());
        if (stone == null) return;
        Ability ability = plugin.abilities().get(stone);
        if (ability == null) return;
        ItemStack held = player.getInventory().getItemInMainHand();
        if (plugin.stones().isStone(held)) return;
        boolean shift = player.isSneaking();
        if (shift) { ability.onSecondary(player, event); }
        else { ability.onPrimary(player, event); }
        event.setCancelled(true);
    }
}
