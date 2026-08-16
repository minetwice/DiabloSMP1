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

public class OmegaAbility extends DiabloAbility {

    public OmegaAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.OMEGA);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        // Ultimate levitation
        player.setVelocity(player.getVelocity().setY(0.6));
        Location center = player.getLocation().add(0, 2.5, 0);
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.2f, 1.0f);

        // 12 Wing displays behind caster
        List<ItemDisplay> wings = new ArrayList<>();
        for (int i = -3; i <= 3; i++) {
            if (i == 0) continue;
            Location wLoc = center.clone().add(i * 0.4, Math.abs(i) * 0.2, -0.5);
            ItemDisplay wing = wLoc.getWorld().spawn(wLoc, ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(Material.FEATHER));
                d.setGravity(false);
            });
            trackDisplay(wing);
            wings.add(wing);
        }

        // Swirling ultimate aura
        scheduleRepeatingTask(() -> {
            Location curr = player.getLocation().add(0, 2.5, 0);
            for (ItemDisplay wing : wings) {
                if (wing.isValid()) wing.teleport(curr);
            }
            plugin.getVisualAndSoundService().spawnDustParticle(curr, Color.fromRGB(255, 0, 0), 2.0f, 10);
            plugin.getVisualAndSoundService().spawnDustParticle(curr, Color.fromRGB(255, 255, 255), 2.0f, 10);
            plugin.getVisualAndSoundService().spawnDustParticle(curr, Color.fromRGB(0, 0, 0), 2.0f, 10);
            curr.getWorld().spawnParticle(Particle.END_ROD, curr, 10, 0.5, 0.5, 0.5, 0.1);
            plugin.getVisualAndSoundService().playSound(curr, config.getChargeSound(), 0.6f, 0.8f);
        }, 0, 2, 60);

        // Rain 15 light swords waves & Final Diablo Requiem Explosion at tick 60
        scheduleTask(() -> {
            Location epicenter = player.getLocation();
            epicenter.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, epicenter, 5, 2.0, 1.0, 2.0);
            epicenter.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, epicenter, 150, 3.0, 2.0, 3.0, 0.3);

            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);
            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation().add(0, 1.0, 0);
                    tLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, tLoc, 1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 2.0);
                }
            } else {
                plugin.getDamageService().dealAoeDamage(player, epicenter, config.getRadius(), config.getFallbackAoeDamage(), 1.8);
            }

            plugin.getVisualAndSoundService().playSound(epicenter, config.getImpactSound(), 1.5f, 0.7f);
            cleanup();
        }, 60);

        return true;
    }
}
