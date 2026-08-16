package com.diablosmp.plugin.ability.impl;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.ability.DiabloAbility;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EmberAbility extends DiabloAbility {

    public EmberAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.EMBER);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        player.setVelocity(player.getVelocity().setY(0.35));
        player.swingMainHand();

        Location center = player.getLocation().add(0, 1.8, 0);
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.0f, 0.9f);

        // 6 crimson meteor fangs orbiting
        List<ItemDisplay> fangs = new ArrayList<>();
        int count = 6;
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            Location loc = center.clone().add(Math.cos(angle) * 1.5, 0, Math.sin(angle) * 1.5);
            ItemDisplay display = loc.getWorld().spawn(loc, ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(Material.FIRE_CHARGE));
                d.setGravity(false);
            });
            trackDisplay(display);
            fangs.add(display);
        }

        // Orbiting phase with hellfire particles
        scheduleRepeatingTask(() -> {
            Location currCenter = player.getLocation().add(0, 1.8, 0);
            for (int i = 0; i < fangs.size(); i++) {
                ItemDisplay display = fangs.get(i);
                if (display != null && display.isValid()) {
                    double angle = (System.currentTimeMillis() / 150.0) + (2 * Math.PI * i / fangs.size());
                    Location newLoc = currCenter.clone().add(Math.cos(angle) * 1.5, Math.sin(angle * 2) * 0.3, Math.sin(angle) * 1.5);
                    display.teleport(newLoc);
                    plugin.getVisualAndSoundService().spawnDustParticle(newLoc, Color.fromRGB(255, 50, 0), 1.5f, 4);
                    newLoc.getWorld().spawnParticle(Particle.FLAME, newLoc, 2, 0.1, 0.1, 0.1, 0.02);
                }
            }
            plugin.getVisualAndSoundService().playSound(currCenter, config.getChargeSound(), 0.5f, 1.1f);
        }, 0, 2, 35);

        // Meteor Dive Impact at tick 35
        scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location targetLoc = target.getLocation().add(0, 0.5, 0);
                    targetLoc.getWorld().spawnParticle(Particle.FLAME, targetLoc, 30, 0.5, 0.5, 0.5, 0.1);
                    targetLoc.getWorld().spawnParticle(Particle.LAVA, targetLoc, 10);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.1);
                    plugin.getVisualAndSoundService().playSound(targetLoc, config.getImpactSound(), 1.0f, 1.0f);
                }
            } else {
                Location aoeLoc = player.getLocation();
                aoeLoc.getWorld().spawnParticle(Particle.EXPLOSION, aoeLoc, 3, 1.0, 0.5, 1.0);
                plugin.getDamageService().dealAoeDamage(player, aoeLoc, config.getRadius(), config.getFallbackAoeDamage(), 1.0);
                plugin.getVisualAndSoundService().playSound(aoeLoc, config.getImpactSound(), 1.0f, 0.9f);
            }

            cleanup();
        }, 35);

        return true;
    }
}
