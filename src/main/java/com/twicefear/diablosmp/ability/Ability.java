package com.twicefear.diablosmp.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * A single ability tied to a stone. Implementations register with
 * {@link AbilityManager}.
 */
public interface Ability {

    /**
     * Triggered on right click while the matching stone is absorbed.
     */
    void onPrimary(Player player, PlayerInteractEvent event);

    /**
     * Triggered on shift + right click while the matching stone is absorbed.
     */
    void onSecondary(Player player, PlayerInteractEvent event);
}
