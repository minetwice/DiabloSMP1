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

import java.util.List;

public class HaloAbility extends DiabloAbility {

    public HaloAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.HALO);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        player.setVelocity(player.getVelocity().setY(0.4));
        Location headLoc = player.getLocation().add(0, 2.5, 0);
        plugin.getVisualAndSoundService().playSound(headLoc, config.getCastSound(), 1.0f, 1.0f);

        // Halo star display
        ItemDisplay haloDisplay = headLoc.getWorld().spawn(headLoc, ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(Material.NETHER_STAR));
            d.setGravity(false);
        });
        trackDisplay(haloDisplay);

        // Corrupted sun rays rotating
        scheduleRepeatingTask(() -> {
            Location current = player.getLocation().add(0, 2.5, 0);
            if (haloDisplay.isValid()) {
                haloDisplay.teleport(current);
            }
            for (int i = 0; i < 8; i++) {
                double angle = (System.currentTimeMillis() / 200.0) + (i * Math.PI / 4.0);
                double rx = Math.cos(angle) * 1.8;
                double rz = Math.sin(angle) * 1.8;
                Location pLoc = current.clone().add(rx, 0, rz);
                plugin.getVisualAndSoundService().spawnDustParticle(pLoc, Color.fromRGB(255, 215, 0), 1.2f, 2);
                plugin.getVisualAndSoundService().spawnDustParticle(pLoc, Color.fromRGB(150, 0, 0), 1.0f, 2);
            }
            plugin.getVisualAndSoundService().playSound(current, config.getChargeSound(), 0.5f, 1.2f);
        }, 0, 2, 40);

        // Solar Light Pillar Burst at tick 40
        scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location targetLoc = target.getLocation();
                    for (double y = 0; y <= 6; y += 0.5) {
                        Location pLoc = targetLoc.clone().add(0, y, 0);
                        plugin.getVisualAndSoundService().spawnDustParticle(pLoc, Color.fromRGB(255, 215, 0), 2.0f, 6);
                        pLoc.getWorld().spawnParticle(Particle.END_ROD, pLoc, 3, 0.2, 0.2, 0.2, 0.05);
                    }
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.0);
                    plugin.getVisualAndSoundService().playSound(targetLoc, config.getImpactSound(), 1.0f, 1.0f);
                }
            } else {
                Location front = player.getLocation().add(player.getLocation().getDirection().multiply(3.0));
                front.getWorld().spawnParticle(Particle.END_ROD, front, 40, 1.0, 1.0, 1.0, 0.1);
                plugin.getDamageService().dealAoeDamage(player, front, config.getRadius(), config.getFallbackAoeDamage(), 1.0);
                plugin.getVisualAndSoundService().playSound(front, config.getImpactSound(), 1.0f, 0.8f);
            }

            cleanup();
        }, 40);

        return true;
    }
}
