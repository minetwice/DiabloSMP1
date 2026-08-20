package me.twicefear.diablosmp.listener;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FirstJoinListener implements Listener {

    private final DiabloSMP plugin;
    private final Random random = new Random();

    public FirstJoinListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getUserManager().loadPlayerData(player);
        UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());

        if (plugin.getConfig().getBoolean("first_join.enabled", true) && !userData.hasReceivedFirstJoinStone()) {
            if (plugin.getSmpManager().isStarted()) {
                userData.setHasReceivedFirstJoinStone(true);
                plugin.getUserManager().savePlayerData(player);
                triggerFirstJoinSequence(player, userData);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        plugin.getUserManager().savePlayerData(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());
            if (userData.isFirstJoinAnimationActive()) {
                event.setCancelled(true);
            }
        }
    }

    public void triggerFirstJoinSequence(Player player, UserData userData) {
        userData.setFirstJoinAnimationActive(true);
        player.setAllowFlight(true);
        player.setFlying(true);

        Location startLoc = player.getLocation();
        StoneType[] stones = StoneType.values();

        // Armorstand popup target
        ArmorStand stand = (ArmorStand) startLoc.getWorld().spawnEntity(startLoc.clone().add(0, 2.5, 0), EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setCustomNameVisible(true);

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 8 * 20; // 8 seconds

            @Override
            public void run() {
                if (!player.isOnline()) {
                    stand.remove();
                    userData.setFirstJoinAnimationActive(false);
                    cancel();
                    return;
                }

                ticks++;

                // Elevate player up to 3 blocks high gently
                Location pLoc = player.getLocation();
                if (ticks <= 40) {
                    pLoc.add(0, 0.075, 0);
                    player.teleport(pLoc);
                }

                // Complex swirling particles around player
                double angle = ticks * 0.3;
                double radius = 1.8;
                double x1 = radius * Math.cos(angle);
                double z1 = radius * Math.sin(angle);
                double x2 = radius * Math.cos(angle + Math.PI);
                double z2 = radius * Math.sin(angle + Math.PI);

                Location currentCenter = player.getLocation().add(0, 1.0, 0);
                currentCenter.getWorld().spawnParticle(Particle.DRAGON_BREATH, currentCenter.clone().add(x1, Math.sin(ticks * 0.1), z1), 3, 0.05, 0.05, 0.05, 0.01);
                currentCenter.getWorld().spawnParticle(Particle.END_ROD, currentCenter.clone().add(x2, Math.cos(ticks * 0.1), z2), 3, 0.05, 0.05, 0.05, 0.01);
                currentCenter.getWorld().spawnParticle(Particle.PORTAL, currentCenter, 10, 0.5, 0.5, 0.5, 0.2);

                // Sound effect pulse
                if (ticks % 5 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 0.8f + (ticks / (float) maxTicks));
                }

                // Stone popup roulette display on armorstand
                if (ticks % 4 == 0) {
                    StoneType randomStone = stones[random.nextInt(stones.length)];
                    stand.setCustomName(randomStone.getDisplayName());
                    stand.getEquipment().setHelmet(plugin.getStoneItemManager().createStoneItem(randomStone));
                }

                stand.teleport(player.getLocation().add(0, 2.2, 0));

                if (ticks >= maxTicks) {
                    stand.remove();
                    userData.setFirstJoinAnimationActive(false);

                    // Descend safely
                    player.setFlying(false);
                    player.setAllowFlight(false);

                    // Choose final stone
                    StoneType chosenStone = stones[random.nextInt(stones.length)];
                    ItemStack stoneStack = plugin.getStoneItemManager().createStoneItem(chosenStone);

                    player.getInventory().addItem(stoneStack);
                    player.sendMessage(ChatColor.GOLD + "========================================");
                    player.sendMessage(ChatColor.YELLOW + "You have been granted a ancient " + chosenStone.getDisplayName() + ChatColor.YELLOW + "!");
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Hold the stone and shift 3 times to absorb its legendary power!");
                    player.sendMessage(ChatColor.GOLD + "========================================");

                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                    player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation().add(0, 1, 0), 2);

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
