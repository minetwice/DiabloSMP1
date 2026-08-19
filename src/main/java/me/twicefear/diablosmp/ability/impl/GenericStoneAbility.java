package me.twicefear.diablosmp.ability.impl;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.ability.DiabloAbility;
import me.twicefear.diablosmp.stone.StoneType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GenericStoneAbility implements DiabloAbility {

    private final DiabloSMP plugin;
    private final StoneType stoneType;

    public GenericStoneAbility(DiabloSMP plugin, StoneType stoneType) {
        this.plugin = plugin;
        this.stoneType = stoneType;
    }

    @Override
    public StoneType getStoneType() {
        return stoneType;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        // Primary ability execution with massive visual and sound feedback
        player.sendMessage(stoneType.getDisplayName() + ChatColor.YELLOW + " activated Primary: " + ChatColor.GOLD + stoneType.getPrimaryAbilityName());
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.0f);

        Location loc = player.getLocation();
        Vector dir = loc.getDirection().normalize();

        // Burst Particles
        triggerParticleEffect(loc, dir);

        // AOE Damage & Effects
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 6, 4, 6)) {
            if (e != player && e instanceof LivingEntity le) {
                le.damage(10.0, player);
                applyStatusEffect(le);
            }
        }
    }

    public void executeSecondary(Player player) {
        // Secondary ability execution
        player.sendMessage(stoneType.getDisplayName() + ChatColor.RED + " activated Secondary: " + ChatColor.DARK_RED + stoneType.getSecondaryAbilityName());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.2f, 0.7f);

        Location loc = player.getLocation();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 60) {
                    cancel();
                    return;
                }
                ticks++;

                triggerPulseParticle(loc, ticks);

                for (Entity e : loc.getWorld().getNearbyEntities(loc, 8, 4, 8)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(2.0, player);
                        applySecondaryStatusEffect(le);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void triggerParticleEffect(Location loc, Vector dir) {
        switch (stoneType) {
            case FROST_MONARCH -> loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc.add(dir.multiply(2)), 100, 1, 1, 1, 0.1);
            case LIGHTNING_OVERLORD -> {
                loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 150, 1, 1, 1, 0.2);
                loc.getWorld().strikeLightningEffect(loc);
            }
            case SHADOW_REAPER -> loc.getWorld().spawnParticle(Particle.SQUID_INK, loc, 80, 1, 1, 1, 0.1);
            case VENOM_HYDRA -> loc.getWorld().spawnParticle(Particle.ITEM_SLIME, loc, 100, 1, 1, 1, 0.1);
            case CELESTIAL_WARDEN -> loc.getWorld().spawnParticle(Particle.END_ROD, loc, 120, 1, 1, 1, 0.1);
            case WIND_TEMPEST -> loc.getWorld().spawnParticle(Particle.CLOUD, loc, 100, 1, 1, 1, 0.1);
            case BLOOD_BERSERKER -> loc.getWorld().spawnParticle(Particle.BLOCK, loc, 100, 1, 1, 1, 0.1, org.bukkit.Material.REDSTONE_BLOCK.createBlockData());
            case GRAVITY_MASTER -> loc.getWorld().spawnParticle(Particle.PORTAL, loc, 120, 1, 1, 1, 0.5);
            case TIME_WEAVER -> loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 150, 1, 1, 1, 0.5);
            case PHANTOM_ASSASSIN -> loc.getWorld().spawnParticle(Particle.SMOKE, loc, 100, 1, 1, 1, 0.1);
            case IRON_TITAN -> loc.getWorld().spawnParticle(Particle.CRIT, loc, 120, 1, 1, 1, 0.2);
            case CHAOS_ARCHON -> loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 3, 1, 1, 1, 0.1);
            default -> loc.getWorld().spawnParticle(Particle.WITCH, loc, 50, 1, 1, 1, 0.1);
        }
    }

    private void triggerPulseParticle(Location loc, int ticks) {
        double radius = (ticks % 20) * 0.4;
        for (int i = 0; i < 360; i += 30) {
            double rad = Math.toRadians(i);
            Location pLoc = loc.clone().add(radius * Math.cos(rad), 0.5, radius * Math.sin(rad));
            loc.getWorld().spawnParticle(Particle.WITCH, pLoc, 2, 0.05, 0.05, 0.05, 0.01);
        }
    }

    private void applyStatusEffect(LivingEntity le) {
        switch (stoneType) {
            case FROST_MONARCH -> le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
            case LIGHTNING_OVERLORD -> le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
            case SHADOW_REAPER -> le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
            case VENOM_HYDRA -> le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 1));
            case BLOOD_BERSERKER -> le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 80, 1));
            default -> le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0));
        }
    }

    private void applySecondaryStatusEffect(LivingEntity le) {
        switch (stoneType) {
            case FROST_MONARCH -> le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 4));
            case VENOM_HYDRA -> le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2));
            case GRAVITY_MASTER -> le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 2));
            case TIME_WEAVER -> le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 10)); // Time stasis
            default -> le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1));
        }
    }
}
