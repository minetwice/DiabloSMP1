package com.twicefear.diablosmp.ability.impl;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import com.twicefear.diablosmp.util.Particles;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Shadow stone.
 * Primary: Shadow dash with afterimages. Secondary: Shadow realm (invisible, bonus damage).
 */
public class ShadowAbility extends AbstractAbility {

    public ShadowAbility(DiabloSMP plugin) {
        super(plugin, StoneType.SHADOW);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location start = player.getLocation();
        Vector dir = start.getDirection().setY(0).normalize();
        World world = start.getWorld();

        new BukkitRunnable() {
            int steps = 0;
            Location prev = start.clone();
            @Override
            public void run() {
                if (steps > 8) { cancel(); return; }
                Location next = prev.clone().add(dir.clone().multiply(2));
                if (next.getBlock().getType().isSolid()) { cancel(); return; }
                Particles.line(prev, next, Particle.DUST, color(), 10);
                Particles.sphere(next, 1, Particle.SMOKE, color(), 8);
                for (Entity e : world.getNearbyEntities(next, 1.5, 2, 1.5)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        le.damage(4, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                    }
                }
                prev = next;
                steps++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        player.teleport(start.add(dir.multiply(16)));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
    }

    @Override
    public void onSecondary(Player player, PlayerInteractEvent event) {
        if (secondaryOnCooldown(player)) return;
        startSecondaryCooldown(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 160, 1));
        player.setMetadata("diablo_shadow_bonus", new org.bukkit.metadata.FixedMetadataValue(plugin, true));

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 160 || !player.isOnline()) {
                    player.removeMetadata("diablo_shadow_bonus", plugin);
                    cancel();
                    return;
                }
                Particles.orbit(player.getLocation().add(0, 1, 0), 1.2, 0, ticks * 0.3,
                        Particle.DUST, color(), 6);
                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 1, 0.2, 0.5, 0.2, 0.01);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
