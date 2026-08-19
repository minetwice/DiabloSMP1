package com.twicefear.diablosmp.ability.impl;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.stone.StoneType;
import org.bukkit.entity.Player;

/**
 * Shared base for all 15 stone abilities.
 */
public abstract class AbstractAbility implements Ability {

    protected final DiabloSMP plugin;
    protected final StoneType type;

    protected AbstractAbility(DiabloSMP plugin, StoneType type) {
        this.plugin = plugin;
        this.type = type;
    }

    protected boolean primaryOnCooldown(Player p) {
        return !plugin.cooldowns().isPrimaryReady(p.getUniqueId());
    }

    protected boolean secondaryOnCooldown(Player p) {
        return !plugin.cooldowns().isSecondaryReady(p.getUniqueId());
    }

    protected void startPrimaryCooldown(Player p) {
        int total = plugin.config().cooldownFor(type.id())[0];
        plugin.cooldowns().setPrimary(p.getUniqueId(), total);
    }

    protected void startSecondaryCooldown(Player p) {
        int total = plugin.config().cooldownFor(type.id())[1];
        plugin.cooldowns().setSecondary(p.getUniqueId(), total);
    }

    protected org.bukkit.Color color() {
        return plugin.stones().particleColor(type);
    }
}
