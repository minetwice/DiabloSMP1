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

public class FrostAbility extends DiabloAbility {

    public FrostAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.FROST);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        player.setVelocity(player.getVelocity().setY(0.3));
        Location center = player.getLocation();
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.0f, 1.3f);

        // Expanding frost nova ring
        scheduleRepeatingTask(() -> {
            for (int r = 1; r <= 8; r++) {
                double radCount = r * 8;
                for (int i = 0; i < radCount; i++) {
                    double angle = 2 * Math.PI * i / radCount;
                    Location pLoc = center.clone().add(Math.cos(angle) * r, 0.2, Math.sin(angle) * r);
                    plugin.getVisualAndSoundService().spawnDustParticle(pLoc, Color.fromRGB(0, 255, 255), 1.2f, 2);
                    pLoc.getWorld().spawnParticle(Particle.SNOWFLAKE, pLoc, 1, 0.1, 0.1, 0.1, 0.01);
                }
            }
            plugin.getVisualAndSoundService().playSound(center, config.getChargeSound(), 0.5f, 1.5f);
        }, 0, 4, 20);

        // Ice Spikes eruption at tick 20
        scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation();
                    ItemDisplay spike = tLoc.getWorld().spawn(tLoc, ItemDisplay.class, d -> {
                        d.setItemStack(new ItemStack(Material.PACKED_ICE));
                        d.setGravity(false);
                    });
                    trackDisplay(spike);

                    tLoc.getWorld().spawnParticle(Particle.BLOCK, tLoc, 30, 0.3, 0.5, 0.3, 0.1, Material.PACKED_ICE.createBlockData());
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 0.5);
                    plugin.getVisualAndSoundService().playSound(tLoc, config.getImpactSound(), 1.0f, 1.2f);
                }
            } else {
                center.getWorld().spawnParticle(Particle.SNOWFLAKE, center, 100, 3.0, 1.0, 3.0, 0.1);
                plugin.getDamageService().dealAoeDamage(player, center, config.getRadius(), config.getFallbackAoeDamage(), 0.5);
            }

            scheduleTask(this::cleanup, 20);
        }, 20);

        return true;
    }
}
