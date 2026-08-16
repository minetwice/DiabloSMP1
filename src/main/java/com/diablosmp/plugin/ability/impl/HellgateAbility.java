package com.diablosmp.plugin.ability.impl;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.ability.AbilityCast;
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

import java.util.ArrayList;
import java.util.List;

public class HellgateAbility extends DiabloAbility {

    public HellgateAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.HELLGATE);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        AbilityCast cast = new AbilityCast(plugin);

        player.swingMainHand();
        Location baseLoc = player.getLocation();
        plugin.getVisualAndSoundService().playSound(baseLoc, "ENTITY_WITHER_SPAWN", 1.0f, 0.8f);

        // Black-red ground circle
        for (int i = 0; i < 360; i += 20) {
            double rad = Math.toRadians(i);
            Location pLoc = baseLoc.clone().add(Math.cos(rad) * 3.0, 0.1, Math.sin(rad) * 3.0);
            plugin.getVisualAndSoundService().spawnDustParticle(pLoc, Color.fromRGB(255, 0, 0), 1.8f, 5);
            pLoc.getWorld().spawnParticle(Particle.FLAME, pLoc, 2, 0.1, 0.1, 0.1, 0.01);
        }

        // 3 Hellgate displays
        List<ItemDisplay> gates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double angle = i * 2 * Math.PI / 3.0;
            Location gateLoc = baseLoc.clone().add(Math.cos(angle) * 3.0, 0, Math.sin(angle) * 3.0);
            ItemDisplay gate = gateLoc.getWorld().spawn(gateLoc, ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(Material.NETHER_BRICK));
                d.setGravity(false);
            });
            cast.trackDisplay(gate);
            gates.add(gate);
        }

        List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

        // Chain drag & lava prison phase (Ticks 0 - 40)
        cast.scheduleRepeatingTask(() -> {
            for (LivingEntity target : targets) {
                if (target.isValid()) {
                    Vector pull = baseLoc.toVector().subtract(target.getLocation().toVector());
                    if (pull.lengthSquared() > 0.001) {
                        pull.normalize().multiply(0.3).setY(0.1);
                        target.setVelocity(pull);
                    }
                    Location tLoc = target.getLocation();
                    plugin.getVisualAndSoundService().spawnDustParticle(tLoc, Color.fromRGB(150, 0, 0), 1.5f, 4);
                    tLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, tLoc, 3, 0.2, 0.2, 0.2, 0.01);
                }
            }
            plugin.getVisualAndSoundService().playSound(baseLoc, "ITEM_ARMOR_EQUIP_CHAIN", 0.8f, 0.8f);
        }, 0, 2, 40);

        // Demonic Claw Slam & Gate Collapse at tick 40
        cast.scheduleTask(() -> {
            baseLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, baseLoc, 3);
            baseLoc.getWorld().spawnParticle(Particle.LAVA, baseLoc, 40, 2.0, 1.0, 2.0);

            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation().add(0, 1.0, 0);
                    tLoc.getWorld().spawnParticle(Particle.EXPLOSION, tLoc, 1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.4);
                    plugin.getVisualAndSoundService().playSound(tLoc, "ENTITY_DRAGON_FIREBALL_EXPLODE", 1.0f, 0.9f);
                }
            } else {
                plugin.getDamageService().dealAoeDamage(player, baseLoc, config.getRadius(), config.getFallbackAoeDamage(), 1.2);
            }

            cast.cleanup();
        }, 40);

        return true;
    }
}
