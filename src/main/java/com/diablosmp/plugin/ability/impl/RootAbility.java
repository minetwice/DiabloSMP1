package com.diablosmp.plugin.ability.impl;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.ability.DiabloAbility;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class RootAbility extends DiabloAbility {

    public RootAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.ROOT);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        player.swingMainHand();
        Location baseLoc = player.getLocation();
        plugin.getVisualAndSoundService().playSound(baseLoc, config.getCastSound(), 1.0f, 0.8f);

        // Ground vine circle
        for (int i = 0; i < 360; i += 15) {
            double rad = Math.toRadians(i);
            Location ringLoc = baseLoc.clone().add(Math.cos(rad) * 3.0, 0.1, Math.sin(rad) * 3.0);
            plugin.getVisualAndSoundService().spawnDustParticle(ringLoc, Color.fromRGB(85, 255, 85), 1.5f, 5);
        }

        List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

        // Trap targets in root vines (immobilization)
        for (LivingEntity target : targets) {
            target.setVelocity(new Vector(0, -0.2, 0));
            Location tLoc = target.getLocation();
            for (double y = 0; y <= 2.0; y += 0.3) {
                Location vine = tLoc.clone().add(0, y, 0);
                plugin.getVisualAndSoundService().spawnDustParticle(vine, Color.fromRGB(0, 100, 0), 1.8f, 6);
                vine.getWorld().spawnParticle(Particle.COMPOSTER, vine, 3, 0.2, 0.2, 0.2, 0.01);
            }
        }

        // Repeating hold and leaf storm charge
        scheduleRepeatingTask(() -> {
            for (LivingEntity target : targets) {
                if (target.isValid()) {
                    target.setVelocity(new Vector(0, -0.1, 0));
                    Location tLoc = target.getLocation();
                    plugin.getVisualAndSoundService().spawnDustParticle(tLoc, Color.fromRGB(0, 150, 50), 1.5f, 4);
                }
            }
            plugin.getVisualAndSoundService().playSound(baseLoc, config.getChargeSound(), 0.5f, 1.3f);
        }, 0, 2, 25);

        // Root Burst damage at tick 25
        scheduleTask(() -> {
            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation().add(0, 1.0, 0);
                    tLoc.getWorld().spawnParticle(Particle.EXPLOSION, tLoc, 1);
                    tLoc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, tLoc, 25, 0.5, 0.5, 0.5, 0.1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.0);
                    plugin.getVisualAndSoundService().playSound(tLoc, config.getImpactSound(), 1.0f, 1.0f);
                }
            } else {
                baseLoc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, baseLoc, 50, 2.0, 1.0, 2.0, 0.1);
                plugin.getDamageService().dealAoeDamage(player, baseLoc, config.getRadius(), config.getFallbackAoeDamage(), 1.0);
            }

            cleanup();
        }, 25);

        return true;
    }
}
