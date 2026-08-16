package com.diablosmp.plugin.ability.impl;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.ability.AbilityCast;
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

public class VertigoAbility extends DiabloAbility {

    public VertigoAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.VERTIGO);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        AbilityCast cast = new AbilityCast(plugin);

        Location center = player.getLocation();
        plugin.getVisualAndSoundService().playSound(center, "ENTITY_SONIC_BOOM", 1.0f, 0.8f);

        // Expanding circular gravity wave
        cast.scheduleRepeatingTask(() -> {
            for (int r = 1; r <= 10; r++) {
                double radCount = r * 8;
                for (int i = 0; i < radCount; i++) {
                    double angle = 2 * Math.PI * i / radCount;
                    Location pLoc = center.clone().add(Math.cos(angle) * r, 0.2, Math.sin(angle) * r);
                    plugin.getVisualAndSoundService().spawnDustParticle(pLoc, Color.fromRGB(170, 0, 170), 1.5f, 2);
                    pLoc.getWorld().spawnParticle(Particle.PORTAL, pLoc, 1, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }, 0, 2, 20);

        List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

        // Float state & Dinnerbone upside-down visual (Ticks 0 - 50)
        for (LivingEntity target : targets) {
            target.setVelocity(new Vector(0, 0.35, 0));

            // Set custom name Dinnerbone temporarily for upside-down visual
            String originalName = target.getCustomName();
            boolean originalNameVisible = target.isCustomNameVisible();

            if (target instanceof Player targetPlayer) {
                targetPlayer.sendTitle("", "§d§lGRAVITY FLIPPED!", 5, 40, 5);
            } else {
                target.setCustomName("Dinnerbone");
                target.setCustomNameVisible(false);
            }

            cast.scheduleTask(() -> {
                if (target.isValid()) {
                    if (!(target instanceof Player)) {
                        target.setCustomName(originalName);
                        target.setCustomNameVisible(originalNameVisible);
                    }
                    target.setVelocity(new Vector(0, -1.2, 0));
                }
            }, 50);
        }

        // Landing Slam Shockwave at tick 55
        cast.scheduleTask(() -> {
            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation();
                    tLoc.getWorld().spawnParticle(Particle.SONIC_BOOM, tLoc, 1);
                    tLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, tLoc, 1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.5);
                    plugin.getVisualAndSoundService().playSound(tLoc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.2f);
                }
            } else {
                center.getWorld().spawnParticle(Particle.SONIC_BOOM, center, 1);
                plugin.getDamageService().dealAoeDamage(player, center, config.getRadius(), config.getFallbackAoeDamage(), 1.2);
            }

            cast.cleanup();
        }, 55);

        return true;
    }
}
