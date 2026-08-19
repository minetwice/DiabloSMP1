package me.twicefear.diablosmp.ability.impl;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.ability.DiabloAbility;
import me.twicefear.diablosmp.stone.StoneType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ShadowReaperAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public ShadowReaperAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.SHADOW_REAPER;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_GRAY + "[Shadow Reaper] " + ChatColor.RED + "SOUL SCYTHE SWEEP!");
        player.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f, 0.5f);

        Vector dir = loc.getDirection().setY(0).normalize();
        Vector right = new Vector(-dir.getZ(), 0, dir.getX()).normalize();

        for (double a = -Math.PI / 2; a <= Math.PI / 2; a += Math.PI / 16) {
            Vector offset = dir.clone().multiply(Math.cos(a) * 4).add(right.clone().multiply(Math.sin(a) * 4));
            Location pLoc = loc.clone().add(offset).add(0, 1, 0);
            loc.getWorld().spawnParticle(Particle.SQUID_INK, pLoc, 5, 0.1, 0.1, 0.1, 0.05);
            loc.getWorld().spawnParticle(Particle.SMOKE, pLoc, 3, 0.1, 0.1, 0.1, 0.02);
        }

        for (Entity e : loc.getWorld().getNearbyEntities(loc.clone().add(dir.multiply(3)), 4, 3, 4)) {
            if (e != player && e instanceof LivingEntity le) {
                le.damage(12.0, player);
                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 4.0));
            }
        }
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_GRAY + "[Shadow Reaper] " + ChatColor.DARK_PURPLE + "DEATH SHADOW REALM!");
        player.getWorld().playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 1.0f, 0.5f);

        ArmorStand shadowStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        shadowStand.setVisible(false);
        shadowStand.setGravity(false);
        shadowStand.setCustomName(ChatColor.DARK_GRAY + "Shadow Decoy of " + player.getName());

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 2, false, false));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;

                if (!player.isOnline() || ticks >= 160) {
                    shadowStand.getWorld().spawnParticle(Particle.LARGE_SMOKE, shadowStand.getLocation().add(0, 1, 0), 30, 0.5, 0.8, 0.5, 0.1);
                    shadowStand.getWorld().playSound(shadowStand.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.2f);
                    for (Entity e : shadowStand.getNearbyEntities(4, 3, 4)) {
                        if (e != player && e instanceof LivingEntity le) {
                            le.damage(10.0, player);
                            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
                        }
                    }
                    shadowStand.remove();
                    cancel();
                    return;
                }

                shadowStand.getWorld().spawnParticle(Particle.PORTAL, shadowStand.getLocation().add(0, 1, 0), 8, 0.3, 0.6, 0.3, 0.1);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
