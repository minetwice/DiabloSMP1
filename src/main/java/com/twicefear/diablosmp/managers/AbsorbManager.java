package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbsorbManager {

    private final DiabloSMP plugin;
    private final Map<UUID, StoneType> absorbed = new HashMap<>();
    private final Map<UUID, Integer> shiftCount = new HashMap<>();

    public AbsorbManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public boolean hasAbsorbed(Player player) {
        return absorbed.containsKey(player.getUniqueId());
    }

    public StoneType getAbsorbed(Player player) {
        return absorbed.get(player.getUniqueId());
    }

    public void setAbsorbed(Player player, StoneType type) {
        absorbed.put(player.getUniqueId(), type);
        resetShift(player);
    }

    public void removeAbsorbed(Player player) {
        absorbed.remove(player.getUniqueId());
        resetShift(player);
    }

    public int getShiftCount(Player player) {
        return shiftCount.getOrDefault(player.getUniqueId(), 0);
    }

    public void resetShift(Player player) {
        shiftCount.put(player.getUniqueId(), 0);
    }

    public void incrementShift(Player player) {
        int count = getShiftCount(player) + 1;
        shiftCount.put(player.getUniqueId(), count);

        player.sendActionBar(net.kyori.adventure.text.Component.text("§eShift " + count + "/3 to absorb..."));

        if (count >= plugin.getConfigManager().getShiftsRequired()) {
            resetShift(player);
        }
    }

    public void playAbsorbAnimation(Player player, StoneType type) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 60) { // 3 seconds
                    cancel();
                    player.sendMessage(plugin.getConfigManager().getPrefix() +
                            plugin.getConfigManager().getMessage("stone-absorbed").replace("%stone%", type.getColoredName()));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                    return;
                }

                // Orbiting particles
                double angle = ticks * 0.3;
                double radius = 1.5 - (ticks * 0.02);
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                player.getWorld().spawnParticle(type.getParticle(),
                        player.getLocation().add(x, 1 + Math.sin(ticks * 0.2) * 0.5, z),
                        5, 0.1, 0.1, 0.1, 0.02);

                if (ticks % 5 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.5f, 1.5f);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
