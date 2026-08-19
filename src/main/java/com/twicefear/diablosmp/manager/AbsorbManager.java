package com.twicefear.diablosmp.manager;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import com.twicefear.diablosmp.util.Particles;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbsorbManager {

    private final DiabloSMP plugin;
    private final Map<UUID, Integer> shiftCounts = new HashMap<>();
    private final Map<UUID, Long> lastShift = new HashMap<>();

    public AbsorbManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void onShift(Player player, ItemStack heldItem) {
        UUID uuid = player.getUniqueId();
        if (plugin.players().hasAbsorbed(uuid)) return;
        if (plugin.stones().getStoneType(heldItem) == null) return;
        if (plugin.stones().isAbsorbed(heldItem)) return;

        long now = System.currentTimeMillis();
        long last = lastShift.getOrDefault(uuid, 0L);
        if (now - last > 3000) {
            shiftCounts.put(uuid, 1);
        } else {
            shiftCounts.merge(uuid, 1, Integer::sum);
        }
        lastShift.put(uuid, now);

        int count = shiftCounts.getOrDefault(uuid, 0);
        int required = plugin.config().absorbShifts();
        player.sendMessage(plugin.messages().prefixed("absorb-shift",
                "count", String.valueOf(count), "required", String.valueOf(required)));

        player.sendActionBar(net.kyori.adventure.text.Component.text(
                "\u00a7e" + count + "\u00a77/" + required + " \u00a77shifts to absorb"));

        if (count >= required) {
            shiftCounts.put(uuid, 0);
            openAbsorbMenu(player, heldItem);
        }
    }

    public void openAbsorbMenu(Player player, ItemStack heldItem) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "Place Stone to Absorb");
        player.openInventory(inv);
        player.sendMessage(plugin.messages().prefixed("absorb-menu-open"));
    }

    public void absorb(Player player, StoneType type) {
        UUID uuid = player.getUniqueId();
        player.closeInventory();

        Location loc = player.getLocation().clone().add(0, 1, 0);
        World world = loc.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;
            double radius = 3;
            @Override
            public void run() {
                int duration = plugin.config().absorbAnimation() * 20;
                if (ticks > duration) {
                    plugin.players().setAbsorbedStone(uuid, type);
                    Particles.burst(player.getLocation().add(0, 1, 0), Particle.DUST,
                            plugin.stones().particleColor(type), 60, 0.5);
                    Particles.sphere(player.getLocation().add(0, 1, 0), 2, Particle.END_ROD, null, 30);
                    world.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.5f, 1.5f);
                    player.sendMessage(plugin.messages().prefixed("absorb-success", "stone", type.display()));
                    player.sendTitle(plugin.messages().plain("join-complete-title"),
                            plugin.messages().plain("join-complete-subtitle"), 10, 40, 10);
                    cancel();
                    return;
                }
                angle += 0.4;
                radius = Math.max(0.5, radius - 0.03);
                for (int i = 0; i < 6; i++) {
                    double a = angle + (Math.PI * 2 * i / 6);
                    Location p = loc.clone().add(Math.cos(a) * radius, Math.sin(ticks * 0.2) * 0.5 + 1, Math.sin(a) * radius);
                    world.spawnParticle(Particle.DUST, p, 2, 0, 0, 0,
                            new Particle.DustOptions(plugin.stones().particleColor(type), 1.3f));
                    world.spawnParticle(Particle.END_ROD, p, 1, 0, 0, 0, 0.01);
                }
                Particles.spiral(loc, 1.5, 2, 2, Particle.DUST, plugin.stones().particleColor(type), 15);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void resetShifts(UUID uuid) {
        shiftCounts.remove(uuid);
        lastShift.remove(uuid);
    }
}
