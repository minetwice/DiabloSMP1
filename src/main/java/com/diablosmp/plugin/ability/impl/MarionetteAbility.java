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

public class MarionetteAbility extends DiabloAbility {

    public MarionetteAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.MARIONETTE);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        AbilityCast cast = new AbilityCast(plugin);

        player.swingMainHand();
        Location playerLoc = player.getLocation();
        plugin.getVisualAndSoundService().playSound(playerLoc, "BLOCK_CONDUIT_ACTIVATE", 1.0f, 0.6f);

        List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

        // Tether particle lines connecting caster hand to targets
        cast.scheduleRepeatingTask(() -> {
            Location handLoc = player.getEyeLocation().add(0, -0.3, 0);
            for (LivingEntity target : targets) {
                if (target.isValid()) {
                    Location targetLoc = target.getLocation().add(0, 1.0, 0);
                    Vector line = targetLoc.toVector().subtract(handLoc.toVector());
                    double dist = handLoc.distance(targetLoc);
                    if (dist > 0.001) {
                        int points = (int) (dist * 3);
                        for (int i = 0; i <= points; i++) {
                            Location point = handLoc.clone().add(line.clone().multiply((double) i / points));
                            plugin.getVisualAndSoundService().spawnDustParticle(point, Color.fromRGB(255, 0, 0), 1.2f, 1);
                        }
                    }
                }
            }
        }, 0, 2, 70);

        // PHASE 1: Pull Together (Ticks 0 - 20)
        cast.scheduleTask(() -> {
            for (LivingEntity target : targets) {
                if (target.isValid()) {
                    Vector pull = playerLoc.toVector().subtract(target.getLocation().toVector());
                    if (pull.lengthSquared() > 0.001) {
                        pull.normalize().multiply(0.4).setY(0.15);
                        target.setVelocity(pull);
                    }
                    plugin.getDamageService().dealDamage(player, target, 2.0, 0.5);
                }
            }
            plugin.getVisualAndSoundService().playSound(playerLoc, "ITEM_ARMOR_EQUIP_CHAIN", 1.0f, 1.2f);
        }, 10);

        // PHASE 2: Side Slam (Ticks 20 - 45)
        cast.scheduleTask(() -> {
            for (int i = 0; i < targets.size(); i++) {
                LivingEntity target = targets.get(i);
                if (target.isValid()) {
                    Vector side = (i % 2 == 0) ? playerLoc.getDirection().crossProduct(new Vector(0, 1, 0)) : playerLoc.getDirection().crossProduct(new Vector(0, -1, 0));
                    side.normalize().multiply(0.8).setY(-0.3);
                    target.setVelocity(side);
                    plugin.getDamageService().dealDamage(player, target, 6.0, 1.0);
                    Location tLoc = target.getLocation();
                    tLoc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, tLoc, 3, 0.3, 0.3, 0.3, 0.05);
                }
            }
            plugin.getVisualAndSoundService().playSound(playerLoc, "ENTITY_PLAYER_ATTACK_SWEEP", 1.0f, 0.8f);
        }, 30);

        // PHASE 3 & 4: Collision Smash & String Snap (Tick 60)
        cast.scheduleTask(() -> {
            playerLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, playerLoc, 2);

            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation().add(0, 1.0, 0);
                    tLoc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, tLoc, 5, 0.5, 0.5, 0.5, 0.1);
                    plugin.getVisualAndSoundService().spawnDustParticle(tLoc, Color.fromRGB(150, 0, 0), 2.5f, 20);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.5);
                    plugin.getVisualAndSoundService().playSound(tLoc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.1f);
                }
            } else {
                plugin.getDamageService().dealAoeDamage(player, playerLoc, config.getRadius(), config.getFallbackAoeDamage(), 1.0);
            }

            cast.cleanup();
        }, 60);

        return true;
    }
}
