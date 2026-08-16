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

public class SeraphAbility extends DiabloAbility {

    public SeraphAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.SERAPH);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        player.setVelocity(player.getVelocity().setY(0.5));
        Location center = player.getLocation().add(0, 1.8, 0);
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.0f, 1.1f);

        // Wing display entities behind caster
        List<ItemDisplay> wings = new ArrayList<>();
        for (int i = -1; i <= 1; i += 2) {
            Location wingLoc = center.clone().add(i * 0.8, 0.2, -0.4);
            ItemDisplay wing = wingLoc.getWorld().spawn(wingLoc, ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(Material.FEATHER));
                d.setGravity(false);
            });
            trackDisplay(wing);
            wings.add(wing);
        }

        // Feather particle wave
        scheduleRepeatingTask(() -> {
            Location currCenter = player.getLocation().add(0, 1.8, 0);
            for (ItemDisplay wing : wings) {
                if (wing.isValid()) wing.teleport(currCenter);
            }
            plugin.getVisualAndSoundService().spawnDustParticle(currCenter, Color.fromRGB(255, 215, 0), 1.5f, 6);
            plugin.getVisualAndSoundService().spawnDustParticle(currCenter, Color.fromRGB(255, 255, 255), 1.5f, 6);
            currCenter.getWorld().spawnParticle(Particle.END_ROD, currCenter, 4, 0.4, 0.4, 0.4, 0.05);
            plugin.getVisualAndSoundService().playSound(currCenter, config.getChargeSound(), 0.5f, 1.3f);
        }, 0, 2, 40);

        // Wave 1 & 2 Feather Blades + Final Corrupted Wing Slam at tick 40
        scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation().add(0, 1.0, 0);
                    tLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, tLoc, 1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.4);
                    plugin.getVisualAndSoundService().playSound(tLoc, config.getImpactSound(), 1.0f, 1.1f);
                }
            } else {
                Location aoeLoc = player.getLocation();
                aoeLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, aoeLoc, 1);
                plugin.getDamageService().dealAoeDamage(player, aoeLoc, config.getRadius(), config.getFallbackAoeDamage(), 1.2);
                plugin.getVisualAndSoundService().playSound(aoeLoc, config.getImpactSound(), 1.0f, 0.9f);
            }

            cleanup();
        }, 40);

        return true;
    }
}
