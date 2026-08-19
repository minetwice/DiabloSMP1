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

public class PhantomAssassinAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public PhantomAssassinAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.PHANTOM_ASSASSIN;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.GRAY + "[Phantom Assassin] " + ChatColor.DARK_GRAY + "SHADOW VEIL!");
        player.getWorld().playSound(loc, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.2f, 1.0f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 120, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 2));

        player.getWorld().spawnParticle(Particle.SQUID_INK, loc.add(0, 1, 0), 20, 0.5, 0.8, 0.5, 0.05);
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.GRAY + "[Phantom Assassin] " + ChatColor.WHITE + "PHANTOM DECOY AMBUSH!");
        player.getWorld().playSound(loc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.2f, 0.8f);

        ArmorStand decoy = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        decoy.setVisible(false);
        decoy.setCustomName(player.getName());
        decoy.setCustomNameVisible(true);

        Vector dash = loc.getDirection().multiply(2.0);
        player.setVelocity(dash);

        new BukkitRunnable() {
            @Override
            public void run() {
                decoy.getWorld().createExplosion(decoy.getLocation(), 2.5f, false, false);
                for (Entity e : decoy.getNearbyEntities(4, 3, 4)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(12.0, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
                    }
                }
                decoy.remove();
            }
        }.runTaskLater(plugin, 40L);
    }
}
