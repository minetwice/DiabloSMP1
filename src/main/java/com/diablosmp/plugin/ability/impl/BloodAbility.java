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

public class BloodAbility extends DiabloAbility {

    public BloodAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.BLOOD);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        player.swingMainHand();
        Location origin = player.getLocation().add(0, 1.5, 0);
        plugin.getVisualAndSoundService().playSound(origin, config.getCastSound(), 1.0f, 0.8f);

        ItemDisplay scythe = origin.getWorld().spawn(origin, ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(Material.REDSTONE));
            d.setGravity(false);
        });
        trackDisplay(scythe);

        // Swirling blood mist and bats
        scheduleRepeatingTask(() -> {
            Location loc = player.getLocation().add(0, 1.2, 0);
            loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 3, 0.8, 0.3, 0.8, 0.05);
            loc.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, loc, 5, 0.5, 0.5, 0.5, 0.1);
            plugin.getVisualAndSoundService().spawnDustParticle(loc, Color.fromRGB(255, 0, 0), 2.0f, 8);
            plugin.getVisualAndSoundService().playSound(loc, config.getChargeSound(), 0.5f, 0.8f);
        }, 0, 2, 30);

        // Crescent Scythe Slash & Lifesteal Heal at tick 30
        scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

            double totalDamageDealt = 0.0;
            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location targetLoc = target.getLocation().add(0, 1.0, 0);
                    targetLoc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, targetLoc, 5, 0.5, 0.5, 0.5, 0.1);
                    plugin.getVisualAndSoundService().spawnDustParticle(targetLoc, Color.fromRGB(180, 0, 0), 2.5f, 15);

                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.2);
                    totalDamageDealt += config.getDirectDamage();
                    plugin.getVisualAndSoundService().playSound(targetLoc, config.getImpactSound(), 1.0f, 0.9f);
                }
            } else {
                Location front = player.getLocation().add(player.getLocation().getDirection().multiply(3.0));
                plugin.getDamageService().dealAoeDamage(player, front, config.getRadius(), config.getFallbackAoeDamage(), 1.0);
                totalDamageDealt += config.getFallbackAoeDamage();
            }

            // 30% Lifesteal
            double lifestealAmount = totalDamageDealt * 0.3;
            double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + lifestealAmount);
            player.setHealth(newHealth);
            plugin.getVisualAndSoundService().spawnDustParticle(player.getLocation().add(0, 1, 0), Color.fromRGB(255, 50, 50), 2.0f, 20);

            cleanup();
        }, 30);

        return true;
    }
}
