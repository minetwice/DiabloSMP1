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

public class MirageAbility extends DiabloAbility {

    public MirageAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.MIRAGE);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        Location origin = player.getLocation();
        plugin.getVisualAndSoundService().playSound(origin, config.getCastSound(), 1.0f, 1.2f);

        // Spawn 4 phantom clone displays
        List<ItemDisplay> clones = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2.0;
            Location cLoc = origin.clone().add(Math.cos(angle) * 2.0, 0, Math.sin(angle) * 2.0);
            ItemDisplay clone = cLoc.getWorld().spawn(cLoc, ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(Material.PHANTOM_MEMBRANE));
                d.setGravity(false);
            });
            trackDisplay(clone);
            clones.add(clone);
        }

        // Phantom dash sequences
        scheduleRepeatingTask(() -> {
            for (ItemDisplay clone : clones) {
                if (clone.isValid()) {
                    Location loc = clone.getLocation();
                    plugin.getVisualAndSoundService().spawnDustParticle(loc, Color.fromRGB(0, 170, 170), 1.5f, 4);
                    loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1);
                }
            }
            plugin.getVisualAndSoundService().playSound(origin, config.getChargeSound(), 0.5f, 1.4f);
        }, 0, 2, 25);

        // Phantom Burst & Multi-dash impact at tick 25
        scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation().add(0, 1.0, 0);
                    tLoc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, tLoc, 3, 0.4, 0.4, 0.4, 0.1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.0);
                    plugin.getVisualAndSoundService().playSound(tLoc, config.getImpactSound(), 1.0f, 1.1f);
                }
            } else {
                Location front = player.getLocation().add(player.getLocation().getDirection().multiply(3.0));
                plugin.getDamageService().dealAoeDamage(player, front, config.getRadius(), config.getFallbackAoeDamage(), 1.0);
            }

            cleanup();
        }, 25);

        return true;
    }
}
