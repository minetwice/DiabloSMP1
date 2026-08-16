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

public class StormAbility extends DiabloAbility {

    public StormAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.STORM);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        player.swingMainHand();
        Location baseLoc = player.getLocation();
        plugin.getVisualAndSoundService().playSound(baseLoc, config.getCastSound(), 1.0f, 1.0f);

        // Electric charge aura
        scheduleRepeatingTask(() -> {
            Location loc = player.getLocation().add(0, 1.2, 0);
            loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 15, 0.5, 0.5, 0.5, 0.1);
            plugin.getVisualAndSoundService().spawnDustParticle(loc, Color.fromRGB(255, 255, 85), 1.5f, 4);
            plugin.getVisualAndSoundService().playSound(loc, config.getChargeSound(), 0.5f, 1.4f);
        }, 0, 2, 20);

        // Chain Lightning strike at tick 20
        scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

            if (!targets.isEmpty()) {
                Location lastLoc = player.getLocation().add(0, 1.5, 0);
                for (LivingEntity target : targets) {
                    Location targetLoc = target.getLocation().add(0, 1.0, 0);

                    // Draw line particle arc
                    Vector dir = targetLoc.toVector().subtract(lastLoc.toVector());
                    double dist = lastLoc.distance(targetLoc);
                    int points = (int) (dist * 4);
                    for (int i = 0; i <= points; i++) {
                        Location point = lastLoc.clone().add(dir.clone().multiply((double) i / points));
                        plugin.getVisualAndSoundService().spawnDustParticle(point, Color.fromRGB(255, 255, 0), 1.5f, 2);
                        point.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, point, 2, 0.1, 0.1, 0.1, 0.05);
                    }

                    targetLoc.getWorld().strikeLightningEffect(targetLoc);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.0);
                    plugin.getVisualAndSoundService().playSound(targetLoc, config.getImpactSound(), 1.0f, 1.0f);
                    lastLoc = targetLoc;
                }
            } else {
                Location front = player.getLocation().add(player.getLocation().getDirection().multiply(4.0));
                front.getWorld().strikeLightningEffect(front);
                plugin.getDamageService().dealAoeDamage(player, front, config.getRadius(), config.getFallbackAoeDamage(), 1.0);
                plugin.getVisualAndSoundService().playSound(front, config.getImpactSound(), 1.0f, 0.9f);
            }

            cleanup();
        }, 20);

        return true;
    }
}
