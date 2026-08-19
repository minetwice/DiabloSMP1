package com.twicefear.diablosmp.listener;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import com.twicefear.diablosmp.util.Particles;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class JoinListener implements Listener {

    private final DiabloSMP plugin;

    public JoinListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.players().hasReceivedReward(player.getUniqueId())) return;
        if (plugin.smp().isIdle()) return;
        startCinematic(player);
    }

    private void startCinematic(Player player) {
        plugin.players().setReceivedReward(player.getUniqueId(), true);
        int duration = plugin.config().joinDuration() * 20;
        Location loc = player.getLocation();
        World world = loc.getWorld();

        player.setInvulnerable(true);
        player.setGameMode(GameMode.ADVENTURE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, 128));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 10));
        player.setAllowFlight(true);
        player.setFlying(true);

        player.sendTitle(plugin.messages().plain("join-title"),
                plugin.messages().plain("join-subtitle"), 10, duration - 20, 10);

        String stoneConfig = plugin.config().joinStone();
        StoneType stone = stoneConfig.equalsIgnoreCase("RANDOM")
                ? StoneType.random()
                : StoneType.byId(stoneConfig).orElse(StoneType.random());

        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;
            double height = 0;
            @Override
            public void run() {
                if (ticks > duration) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.setInvulnerable(false);
                    player.setGameMode(GameMode.SURVIVAL);
                    player.getInventory().addItem(plugin.stones().createStone(stone));
                    player.sendMessage(plugin.messages().prefixed("stone-received", "stone", stone.display()));
                    Particles.burst(loc, Particle.END_ROD, null, 50, 0.5);
                    world.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    cancel();
                    return;
                }
                if (ticks < duration / 2) {
                    height = plugin.config().joinLift() * ((double) ticks / (duration / 2));
                }
                player.setVelocity(new Vector(0, 0.1, 0));
                player.teleport(loc.clone().add(0, height, 0));
                angle += 0.3;

                Location playerLoc = player.getLocation().clone().add(0, 1, 0);
                Particles.ring(playerLoc, 1 + Math.sin(ticks * 0.1) * 0.5, Particle.DUST,
                        Color.fromRGB(255, 0, 0), 20);
                Particles.orbit(playerLoc, 2, 0, -angle, Particle.DUST,
                        Color.fromRGB(200, 0, 0), 12);
                Particles.spiral(playerLoc, 1.5, 3, 2, Particle.FLAME, null, 20);
                Particles.sphere(playerLoc, 2.5, Particle.DUST, Color.fromRGB(150, 0, 0), 10);
                Particles.doubleHelix(playerLoc, 1, 4, 3, Particle.DUST,
                        Color.fromRGB(255, 50, 50), 16);

                for (int i = 0; i < StoneType.values().length; i++) {
                    StoneType st = StoneType.values()[i];
                    double a = angle * 0.5 + (Math.PI * 2 * i / StoneType.values().length);
                    double r = 3 + Math.sin(ticks * 0.05 + i) * 0.5;
                    double y = 1 + Math.sin(ticks * 0.1 + i * 0.5) * 1.5;
                    Location stoneLoc = playerLoc.clone().add(Math.cos(a) * r, y, Math.sin(a) * r);
                    world.spawnParticle(Particle.DUST, stoneLoc, 2, 0, 0, 0,
                            new Particle.DustOptions(plugin.stones().particleColor(st), 1.5f));
                    world.spawnParticle(Particle.END_ROD, stoneLoc, 1, 0, 0, 0, 0.01);
                }

                if (ticks % 20 == 0) {
                    world.playSound(loc, Sound.BLOCK_BEACON_AMBIENT, 0.5f, 0.5f + (ticks * 0.01f));
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }
}
