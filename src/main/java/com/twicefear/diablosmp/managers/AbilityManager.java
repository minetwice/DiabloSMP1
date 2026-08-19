package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.abilities.*;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.entity.Player;

public class AbilityManager {

    private final DiabloSMP plugin;
    private final EarthquakeAbility earthquakeAbility;
    private final InfernalAbility infernalAbility;
    private final TempestAbility tempestAbility;
    private final ShadowAbility shadowAbility;
    private final FrostbiteAbility frostbiteAbility;

    public AbilityManager(DiabloSMP plugin) {
        this.plugin = plugin;
        this.earthquakeAbility = new EarthquakeAbility(plugin);
        this.infernalAbility = new InfernalAbility(plugin);
        this.tempestAbility = new TempestAbility(plugin);
        this.shadowAbility = new ShadowAbility(plugin);
        this.frostbiteAbility = new FrostbiteAbility(plugin);
    }

    public void execute(Player player, StoneType type, boolean primary) {
        switch (type) {
            case EARTHQUAKE_RELIC -> {
                if (primary) earthquakeAbility.primary(player);
                else earthquakeAbility.secondary(player);
            }
            case INFERNAL_CORE -> {
                if (primary) infernalAbility.primary(player);
                else infernalAbility.secondary(player);
            }
            case TEMPEST_ORB -> {
                if (primary) tempestAbility.primary(player);
                else tempestAbility.secondary(player);
            }
            case SHADOW_FANG -> {
                if (primary) shadowAbility.primary(player);
                else shadowAbility.secondary(player);
            }
            case FROSTBITE_CRYSTAL -> {
                if (primary) frostbiteAbility.primary(player);
                else frostbiteAbility.secondary(player);
            }
            // Remaining stones still under development
            default -> {
                player.sendMessage("§e" + type.getDisplayName() + " §7ability is under development (Primary: " + primary + ")");
                player.getWorld().spawnParticle(type.getParticle(), player.getLocation().add(0, 1, 0), 60, 1.2, 1.2, 1.2, 0.08);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f);
            }
        }
    }
}
