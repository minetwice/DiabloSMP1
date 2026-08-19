package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.abilities.EarthquakeAbility;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.entity.Player;

public class AbilityManager {

    private final DiabloSMP plugin;
    private final EarthquakeAbility earthquakeAbility;

    public AbilityManager(DiabloSMP plugin) {
        this.plugin = plugin;
        this.earthquakeAbility = new EarthquakeAbility(plugin);
    }

    public void execute(Player player, StoneType type, boolean primary) {
        switch (type) {
            case EARTHQUAKE_RELIC -> {
                if (primary) earthquakeAbility.primary(player);
                else earthquakeAbility.secondary(player);
            }
            // TODO: Add other 14 stones abilities here
            default -> {
                player.sendMessage("§e" + type.getDisplayName() + " ability coming soon! (Primary: " + primary + ")");
                // Placeholder particles
                player.getWorld().spawnParticle(type.getParticle(), player.getLocation().add(0, 1, 0), 50, 1, 1, 1, 0.1);
            }
        }
    }
}
