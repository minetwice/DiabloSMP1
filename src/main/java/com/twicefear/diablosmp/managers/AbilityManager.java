package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.abilities.EarthquakeAbility;
import com.twicefear.diablosmp.abilities.InfernalAbility;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.entity.Player;

public class AbilityManager {

    private final DiabloSMP plugin;
    private final EarthquakeAbility earthquakeAbility;
    private final InfernalAbility infernalAbility;

    public AbilityManager(DiabloSMP plugin) {
        this.plugin = plugin;
        this.earthquakeAbility = new EarthquakeAbility(plugin);
        this.infernalAbility = new InfernalAbility(plugin);
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
            // More abilities will be added here
            default -> {
                player.sendMessage("§e" + type.getDisplayName() + " §7ability is under development (Primary: " + primary + ")");
                player.getWorld().spawnParticle(type.getParticle(), player.getLocation().add(0, 1, 0), 60, 1.2, 1.2, 1.2, 0.08);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f);
            }
        }
    }
}
