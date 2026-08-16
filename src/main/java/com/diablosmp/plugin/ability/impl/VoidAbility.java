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

public class VoidAbility extends DiabloAbility {

    public VoidAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.VOID);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        Location center = player.getLocation().add(player.getLocation().getDirection().multiply(3.0)).add(0, 1.5, 0);
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.0f, 0.7f);

        ItemDisplay voidCore = center.getWorld().spawn(center, ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(Material.ECHO_SHARD));
            d.setGravity(false);
        });
        trackDisplay(voidCore);

        // Pulling phase with rift particles
        scheduleRepeatingTask(() -> {
            center.getWorld().spawnParticle(Particle.PORTAL, center, 40, 0.5, 0.5, 0.5, 0.2);
            center.getWorld().spawnParticle(Particle.DRAGON_BREATH, center, 15, 0.3, 0.3, 0.3, 0.05);
            plugin.getVisualAndSoundService().spawnDustParticle(center, Color.fromRGB(170, 0, 170), 2.0f, 10);

            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);
            for (LivingEntity target : targets) {
                Vector pull = center.toVector().subtract(target.getLocation().toVector()).normalize().multiply(0.4);
                pull.setY(0.2);
                target.setVelocity(pull);
            }
            plugin.getVisualAndSoundService().playSound(center, config.getChargeSound(), 0.5f, 0.9f);
        }, 0, 2, 30);

        // Implosion at tick 30
        scheduleTask(() -> {
            center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, center, 2);
            center.getWorld().spawnParticle(Particle.SONIC_BOOM, center, 1);
            plugin.getVisualAndSoundService().playSound(center, config.getImpactSound(), 1.2f, 0.8f);

            plugin.getDamageService().dealAoeDamage(player, center, config.getRadius(), config.getDirectDamage(), 1.3);

            cleanup();
        }, 30);

        return true;
    }
}
