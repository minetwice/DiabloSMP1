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
import org.bukkit.util.Vector;

import java.util.List;

public class GraveAbility extends DiabloAbility {

    public GraveAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.GRAVE);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        Location center = player.getLocation().add(player.getLocation().getDirection().multiply(3.5)).add(0, 1.0, 0);
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.0f, 0.6f);

        ItemDisplay gravityCore = center.getWorld().spawn(center, ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(Material.OBSIDIAN));
            d.setGravity(false);
        });
        trackDisplay(gravityCore);

        // Inward pull & Obsidian gravity charge
        scheduleRepeatingTask(() -> {
            plugin.getVisualAndSoundService().spawnDustParticle(center, Color.fromRGB(85, 0, 85), 2.2f, 12);
            center.getWorld().spawnParticle(Particle.PORTAL, center, 30, 0.8, 0.8, 0.8, 0.1);

            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);
            for (LivingEntity target : targets) {
                Vector pull = center.toVector().subtract(target.getLocation().toVector()).normalize().multiply(0.35);
                pull.setY(0.15);
                target.setVelocity(pull);
            }
            plugin.getVisualAndSoundService().playSound(center, config.getChargeSound(), 0.5f, 0.7f);
        }, 0, 2, 35);

        // Obsidian Pillar Eruption at tick 35
        scheduleTask(() -> {
            center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, center, 2);
            center.getWorld().spawnParticle(Particle.BLOCK, center, 50, 1.0, 1.0, 1.0, 0.2, Material.OBSIDIAN.createBlockData());
            plugin.getDamageService().dealAoeDamage(player, center, config.getRadius(), config.getDirectDamage(), 1.5);
            plugin.getVisualAndSoundService().playSound(center, config.getImpactSound(), 1.2f, 0.6f);

            cleanup();
        }, 35);

        return true;
    }
}
