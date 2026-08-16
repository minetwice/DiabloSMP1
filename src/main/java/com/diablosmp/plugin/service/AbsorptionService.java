package com.diablosmp.plugin.service;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.PlayerData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AbsorptionService {
    private final DiabloSMP plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Set<Player> absorbingPlayers = ConcurrentHashMap.newKeySet();

    public AbsorptionService(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isAbsorbing(Player player) {
        return absorbingPlayers.contains(player);
    }

    public void startAbsorptionCinematic(Player player, DiabloStoneType stoneType, ItemStack heldItem) {
        if (absorbingPlayers.contains(player)) return;

        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
        if (data.hasStone(stoneType)) {
            player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() +
                    "<yellow>You have already absorbed the power of " + stoneType.name() + "!</yellow>"));
            return;
        }

        absorbingPlayers.add(player);

        // Consume 1 physical item
        heldItem.setAmount(heldItem.getAmount() - 1);

        // Disallow fall damage during cinematic
        player.setFallDistance(0);

        Location origin = player.getLocation();
        plugin.getVisualAndSoundService().playSound(origin, "BLOCK_RESPAWN_ANCHOR_CHARGE", 1.2f, 0.8f);

        // PHASE 1: Rise (Ticks 0-20)
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    absorbingPlayers.remove(player);
                    return;
                }

                if (ticks < 20) {
                    player.setVelocity(new Vector(0, 0.15, 0));
                    player.setFallDistance(0);
                    Location pLoc = player.getLocation().add(0, 1.0, 0);
                    plugin.getVisualAndSoundService().spawnDustParticle(pLoc, Color.fromRGB(255, 50, 0), 1.8f, 10);
                    pLoc.getWorld().spawnParticle(Particle.FLAME, pLoc, 5, 0.3, 0.3, 0.3, 0.02);
                } else {
                    cancel();
                    runPhase2(player, stoneType, origin);
                }
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void runPhase2(Player player, DiabloStoneType stoneType, Location origin) {
        // PHASE 2: Pose & Absorb (Ticks 20-70)
        Location chestLoc = player.getLocation().add(0, 1.2, 0);

        // Spawn stone visual moving into chest
        ItemDisplay stoneDisplay = chestLoc.getWorld().spawn(chestLoc, ItemDisplay.class, d -> {
            d.setItemStack(plugin.getCastingService().createStoneItem(stoneType));
            d.setGravity(false);
        });

        Title.Times times = Title.Times.times(Duration.ofMillis(200), Duration.ofMillis(2000), Duration.ofMillis(300));
        player.showTitle(Title.title(
                miniMessage.deserialize("<gradient:#FF5555:#AA0000>ABSORBING POWER</gradient>"),
                miniMessage.deserialize("<gray>Infusing " + stoneType.name() + " into soul...</gray>"),
                times
        ));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    if (stoneDisplay.isValid()) stoneDisplay.remove();
                    cancel();
                    absorbingPlayers.remove(player);
                    return;
                }

                Location currentChest = player.getLocation().add(0, 1.2, 0);
                stoneDisplay.teleport(currentChest);
                player.setVelocity(new Vector(0, 0.02, 0));
                player.setFallDistance(0);

                // Vortex & spiral converging particles
                for (int i = 0; i < 8; i++) {
                    double angle = (ticks * 0.2) + (i * Math.PI / 4.0);
                    double radius = 2.5 - (ticks * 0.04);
                    if (radius < 0.2) radius = 0.2;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    Location particleLoc = currentChest.clone().add(x, (ticks * 0.02) - 0.5, z);

                    plugin.getVisualAndSoundService().spawnDustParticle(particleLoc, Color.fromRGB(255, 0, 0), 1.5f, 3);
                    particleLoc.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0.05, 0.05, 0.05, 0.01);
                }

                plugin.getVisualAndSoundService().playSound(currentChest, "BLOCK_AMETHYST_BLOCK_CHIME", 0.8f, 1.2f);

                ticks += 2;
                if (ticks >= 50) {
                    if (stoneDisplay.isValid()) stoneDisplay.remove();
                    cancel();
                    runPhase3(player, stoneType);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void runPhase3(Player player, DiabloStoneType stoneType) {
        // PHASE 3: Hero Landing & Shockwave (Ticks 70-80)
        player.setVelocity(new Vector(0, -0.8, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    absorbingPlayers.remove(player);
                    return;
                }

                Location landLoc = player.getLocation();
                player.setFallDistance(0);

                // Land Shockwave
                landLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, landLoc, 2);
                landLoc.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, landLoc, 80, 2.0, 0.5, 2.0, 0.2);
                plugin.getVisualAndSoundService().playSound(landLoc, "ENTITY_GENERIC_EXPLODE", 1.2f, 0.7f);

                // PHASE 4: Completion
                PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
                data.addStone(stoneType);
                data.setActiveStone(stoneType);
                plugin.getStorageService().savePlayerData(player.getUniqueId(), true);

                plugin.getHudService().updateHud(player);

                player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() +
                        "<gradient:#55FF55:#00AA00>POWER ABSORBED! You have permanently unlocked " + stoneType.name() + "!</gradient>"));

                absorbingPlayers.remove(player);
            }
        }.runTaskLater(plugin, 8L);
    }
}
