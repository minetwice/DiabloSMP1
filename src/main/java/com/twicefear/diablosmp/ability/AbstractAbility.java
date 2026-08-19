package com.twicefear.diablosmp.ability;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * Base class for all stone abilities. Handles cooldown tracking and
 * provides convenience access to the plugin and stone type.
 */
public abstract class AbstractAbility implements Ability {

    protected final DiabloSMP plugin;
    protected final StoneType stoneType;

    protected AbstractAbility(DiabloSMP plugin, StoneType stoneType) {
        this.plugin = plugin;
        this.stoneType = stoneType;
    }

    public StoneType stoneType() {
        return stoneType;
    }

    public Color color() {
        return plugin.stones().particleColor(stoneType);
    }

    protected boolean primaryOnCooldown(Player player) {
        return !plugin.cooldowns().isPrimaryReady(player.getUniqueId());
    }

    protected boolean secondaryOnCooldown(Player player) {
        return !plugin.cooldowns().isSecondaryReady(player.getUniqueId());
    }

    protected void startPrimaryCooldown(Player player) {
        int seconds = com.twicefear.diablosmp.manager.DiabloCooldowns.primary(stoneType);
        plugin.cooldowns().setPrimary(player.getUniqueId(), seconds);
    }

    protected void startSecondaryCooldown(Player player) {
        int seconds = com.twicefear.diablosmp.manager.DiabloCooldowns.secondary(stoneType);
        plugin.cooldowns().setSecondary(player.getUniqueId(), seconds);
    }
}
