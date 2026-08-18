package com.twicefear.diablosmp.ability;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.api.AethelionHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Abstract base for all abilities.
 * Subclasses implement {@link #onCast} and declare metadata via constructor.
 */
public abstract class Ability {

    protected final DiabloSMP plugin;
    protected final String id;
    protected final String displayName;
    protected final String description;
    protected final String element;
    protected final int cooldownSeconds;

    protected Ability(DiabloSMP plugin, String id, String displayName, String description,
                      String element, int cooldownSeconds) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.element = element;
        this.cooldownSeconds = cooldownSeconds;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getElement() { return element; }
    public int getCooldownSeconds() { return cooldownSeconds; }

    public abstract void onCast(Player player);

    protected void delay(long ticks, Runnable task) {
        Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
    }

    protected BukkitRunnable repeat(long delayTicks, long periodTicks, int maxTicks, TickCallback callback) {
        BukkitRunnable run = new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick >= maxTicks) {
                    cancel();
                    return;
                }
                callback.onTick(tick);
                tick++;
            }
        };
        run.runTaskTimer(plugin, delayTicks, periodTicks);
        return run;
    }

    @FunctionalInterface
    public interface TickCallback {
        void onTick(int tick);
    }

    protected List<LivingEntity> getNearbyLivingEntities(Player player, double radius) {
        return player.getLocation().getNearbyLivingEntities(radius).stream()
                .filter(e -> e != player)
                .filter(e -> e.isValid())
                .toList();
    }

    protected void damageNearby(Player player, double radius, double damage, boolean knockbackUp) {
        for (LivingEntity e : getNearbyLivingEntities(player, radius)) {
            e.damage(damage, player);
            if (knockbackUp) {
                e.setVelocity(e.getVelocity().setY(0.8));
            }
        }
    }

    protected void sound(Location loc, Sound sound, float vol, float pitch) {
        loc.getWorld().playSound(loc, sound, vol, pitch);
    }

    protected void particle(Location loc, Particle particle, int count, double ox, double oy, double oz, double speed) {
        loc.getWorld().spawnParticle(particle, loc, count, ox, oy, oz, speed);
    }

    protected void resetPlayer(Player player) {
        AethelionHook.get().resetScale(player, null);
        AethelionHook.get().stopAllEmotes(player);
    }

    protected void castMessage(Player player) {
        player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.GREEN
                + "Casting: " + net.md_5.bungee.api.ChatColor.GOLD + displayName);
    }
}
