package me.twicefear.diablosmp.ability.impl;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.ability.DiabloAbility;
import me.twicefear.diablosmp.stone.StoneType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EarthSmasherAbility implements DiabloAbility, Listener {

    private final DiabloSMP plugin;
    private final Set<UUID> activeRockHolders = new HashSet<>();
    private final Set<UUID> launchRequested = new HashSet<>();
    private final Set<UUID> domainTrappedPlayers = new HashSet<>();

    public EarthSmasherAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.EARTH_SMASHER;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location targetLoc = player.getTargetBlockExact(15) != null ?
                player.getTargetBlockExact(15).getLocation() : player.getLocation().add(player.getLocation().getDirection().multiply(5));

        Location center = targetLoc.clone().add(0, 1, 0);
        player.sendMessage(ChatColor.GOLD + "[Earth Smasher] " + ChatColor.YELLOW + "Rising giant rock cluster!");
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 0.6f);

        activeRockHolders.add(player.getUniqueId());

        List<ArmorStand> stands = new ArrayList<>();
        Material[] mats = {Material.DIRT, Material.COBBLESTONE, Material.STONE, Material.DEEPSLATE};

        for (int i = 0; i < 8; i++) {
            ArmorStand stand = (ArmorStand) center.getWorld().spawnEntity(center.clone().add(
                    (Math.random() - 0.5) * 2,
                    (Math.random() - 0.5) * 2,
                    (Math.random() - 0.5) * 2
            ), EntityType.ARMOR_STAND);

            stand.setVisible(false);
            stand.setGravity(false);
            stand.getEquipment().setHelmet(new org.bukkit.inventory.ItemStack(mats[i % mats.length]));
            stands.add(stand);
        }

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 160;

            @Override
            public void run() {
                boolean shouldLaunch = launchRequested.remove(player.getUniqueId());

                if (!player.isOnline() || ticks >= maxTicks || shouldLaunch) {
                    activeRockHolders.remove(player.getUniqueId());
                    if (shouldLaunch || ticks >= maxTicks) {
                        launchRockCluster(player, stands);
                    } else {
                        for (ArmorStand s : stands) s.remove();
                    }
                    cancel();
                    return;
                }

                ticks++;

                Location cursor = player.getTargetBlockExact(15) != null ?
                        player.getTargetBlockExact(15).getLocation().add(0, 3, 0) :
                        player.getEyeLocation().add(player.getLocation().getDirection().multiply(8));

                for (int i = 0; i < stands.size(); i++) {
                    ArmorStand s = stands.get(i);
                    double offset = i * (Math.PI / 4) + (ticks * 0.1);
                    Location dest = cursor.clone().add(Math.cos(offset) * 1.2, Math.sin(ticks * 0.05), Math.sin(offset) * 1.2);
                    s.teleport(dest);

                    for (Entity e : s.getNearbyEntities(2, 2, 2)) {
                        if (e != player && e instanceof LivingEntity le) {
                            le.setVelocity(new Vector(0, 0.3, 0));
                        }
                    }
                }

                cursor.getWorld().spawnParticle(Particle.BLOCK, cursor, 15, 0.8, 0.8, 0.8, 0.1, Material.DIRT.createBlockData());
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @EventHandler
    public void onLeftClickLaunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (activeRockHolders.contains(player.getUniqueId()) &&
                (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            launchRequested.add(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "[Earth Smasher] " + ChatColor.RED + "LAUNCHING ROCK CLUSTER!");
        }
    }

    private void launchRockCluster(Player player, List<ArmorStand> stands) {
        Vector dir = player.getLocation().getDirection().multiply(2.2);

        new BukkitRunnable() {
            int flyTicks = 0;

            @Override
            public void run() {
                flyTicks++;
                boolean hit = false;

                for (ArmorStand s : stands) {
                    Location next = s.getLocation().add(dir);
                    s.teleport(next);

                    if (!next.getBlock().isEmpty() || flyTicks > 40) {
                        hit = true;
                    }
                }

                if (stands.isEmpty()) {
                    cancel();
                    return;
                }

                Location leadLoc = stands.get(0).getLocation();
                leadLoc.getWorld().spawnParticle(Particle.EXPLOSION, leadLoc, 5, 0.5, 0.5, 0.5, 0.1);

                if (hit) {
                    leadLoc.getWorld().createExplosion(leadLoc, 4.0f, false, false);
                    leadLoc.getWorld().playSound(leadLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);

                    for (Entity e : leadLoc.getWorld().getNearbyEntities(leadLoc, 5, 5, 5)) {
                        if (e != player && e instanceof LivingEntity le) {
                            le.damage(14.0, player);
                        }
                    }

                    for (ArmorStand s : stands) s.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_RED + "[Earth Smasher] " + ChatColor.RED + "SUMMONING TERRESTRIAL DOMAIN!");
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);

        Location domainCenter = loc.clone().add(0, 150, 0);

        // Build temporary glass platform boxing arena
        buildDomainArena(domainCenter);

        List<LivingEntity> trapped = new ArrayList<>();
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 7, 5, 7)) {
            if (e instanceof LivingEntity le) {
                trapped.add(le);
                if (le instanceof Player p) {
                    domainTrappedPlayers.add(p.getUniqueId());
                    p.sendMessage(ChatColor.DARK_PURPLE + "You have been trapped in the Earth Domain! Ender pearls and Chorus fruits are disabled!");
                }
            }
        }

        for (LivingEntity le : trapped) {
            le.teleport(domainCenter.clone().add(0, 1, 0));
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2));
        }

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;

                if (trapped.contains(player)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                }

                if (ticks >= 200) { // 10 seconds
                    for (LivingEntity le : trapped) {
                        le.teleport(loc);
                        if (le instanceof Player p) {
                            domainTrappedPlayers.remove(p.getUniqueId());
                        }
                    }
                    removeDomainArena(domainCenter);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void buildDomainArena(Location center) {
        int r = 6;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                center.clone().add(x, 0, z).getBlock().setType(Material.TINTED_GLASS);
                center.clone().add(x, 5, z).getBlock().setType(Material.TINTED_GLASS);
            }
        }
        for (int y = 1; y < 5; y++) {
            for (int i = -r; i <= r; i++) {
                center.clone().add(i, y, r).getBlock().setType(Material.BARRIER);
                center.clone().add(i, y, -r).getBlock().setType(Material.BARRIER);
                center.clone().add(r, y, i).getBlock().setType(Material.BARRIER);
                center.clone().add(-r, y, i).getBlock().setType(Material.BARRIER);
            }
        }
    }

    private void removeDomainArena(Location center) {
        int r = 6;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                for (int y = 0; y <= 5; y++) {
                    center.clone().add(x, y, z).getBlock().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onPearlTeleport(PlayerTeleportEvent event) {
        if (domainTrappedPlayers.contains(event.getPlayer().getUniqueId())) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Ender pearls cannot be used inside the Earth Domain!");
            }
        }
    }

    @EventHandler
    public void onChorusEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.CHORUS_FRUIT && domainTrappedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Chorus fruits cannot be eaten inside the Earth Domain!");
        }
    }
}
