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
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;

public class ShardAbility extends DiabloAbility {

    public ShardAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.SHARD);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        // 1. Initial launch upward and levitation-like feel
        player.setVelocity(player.getVelocity().setY(0.45));
        player.swingMainHand();

        Location center = player.getLocation().add(0, 2.2, 0);
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.0f, 1.2f);

        // 2. Spawn 5 azure shard display entities in a ring
        List<ItemDisplay> shards = new ArrayList<>();
        int shardCount = 5;
        for (int i = 0; i < shardCount; i++) {
            double angle = 2 * Math.PI * i / shardCount;
            Location loc = center.clone().add(Math.cos(angle) * 1.2, 0, Math.sin(angle) * 1.2);
            ItemDisplay display = loc.getWorld().spawn(loc, ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(Material.AMETHYST_SHARD));
                d.setGravity(false);
            });
            trackDisplay(display);
            shards.add(display);
        }

        // 3. Hovering particle ring and rotating shards
        scheduleRepeatingTask(() -> {
            Location currCenter = player.getLocation().add(0, 2.2, 0);
            for (int i = 0; i < shards.size(); i++) {
                ItemDisplay display = shards.get(i);
                if (display != null && display.isValid()) {
                    double angle = (System.currentTimeMillis() / 200.0) + (2 * Math.PI * i / shards.size());
                    Location newLoc = currCenter.clone().add(Math.cos(angle) * 1.2, 0, Math.sin(angle) * 1.2);
                    display.teleport(newLoc);
                    plugin.getVisualAndSoundService().spawnDustParticle(newLoc, Color.fromRGB(85, 255, 255), 1.2f, 3);
                }
            }
            plugin.getVisualAndSoundService().playSound(currCenter, config.getChargeSound(), 0.6f, 1.5f);
        }, 0, 2, 40);

        // 4. Target detection & smash impact at tick 40
        scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);
            Location playerLoc = player.getLocation();

            if (!targets.isEmpty()) {
                for (int i = 0; i < targets.size(); i++) {
                    LivingEntity target = targets.get(i);
                    Location targetLoc = target.getLocation().add(0, 1, 0);

                    plugin.getVisualAndSoundService().spawnDustParticle(targetLoc, Color.fromRGB(0, 200, 255), 2.0f, 20);
                    targetLoc.getWorld().spawnParticle(Particle.EXPLOSION, targetLoc, 1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.2);
                    plugin.getVisualAndSoundService().playSound(targetLoc, config.getImpactSound(), 1.0f, 1.0f);
                }
            } else {
                // Fallback smash forward on ground
                Location front = playerLoc.add(playerLoc.getDirection().multiply(4.0));
                front.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, front, 1);
                plugin.getDamageService().dealAoeDamage(player, front, 4.0, config.getFallbackAoeDamage(), 1.0);
                plugin.getVisualAndSoundService().playSound(front, config.getImpactSound(), 1.0f, 0.8f);
            }

            // Clean display entities
            cleanup();
        }, 40);

        return true;
    }
}
