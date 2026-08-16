package com.diablosmp.plugin.ability.impl;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.ability.AbilityCast;
import com.diablosmp.plugin.ability.DiabloAbility;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class QuakeAbility extends DiabloAbility {

    private static final Set<Material> ALLOWED_MATERIALS = EnumSet.of(
            Material.STONE, Material.DIRT, Material.GRASS_BLOCK, Material.COBBLESTONE,
            Material.DEEPSLATE, Material.SAND, Material.GRAVEL, Material.NETHERRACK,
            Material.BLACKSTONE, Material.BASALT, Material.MUD, Material.CLAY
    );

    public QuakeAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.QUAKE);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        AbilityCast cast = new AbilityCast(plugin);

        player.swingMainHand();
        Location playerLoc = player.getLocation();
        plugin.getVisualAndSoundService().playSound(playerLoc, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR", 1.2f, 0.5f);

        // Ground punch particles
        playerLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, playerLoc, 1);

        // Find ground blocks in front of player
        Vector dir = playerLoc.getDirection().setY(0).normalize();
        List<Block> selectedBlocks = new ArrayList<>();
        Map<Block, BlockState> savedStates = new HashMap<>();

        for (int x = -2; x <= 2; x++) {
            for (int z = 1; z <= 4; z++) {
                Location checkLoc = playerLoc.clone().add(dir.clone().multiply(z)).add(x, -1, 0);
                Block block = checkLoc.getBlock();
                if (ALLOWED_MATERIALS.contains(block.getType()) && selectedBlocks.size() < 12) {
                    selectedBlocks.add(block);
                    savedStates.put(block, block.getState());
                }
            }
        }

        if (selectedBlocks.isEmpty()) {
            Block base = playerLoc.clone().add(0, -1, 0).getBlock();
            if (ALLOWED_MATERIALS.contains(base.getType())) {
                selectedBlocks.add(base);
                savedStates.put(base, base.getState());
            }
        }

        List<BlockDisplay> displays = new ArrayList<>();
        for (Block b : selectedBlocks) {
            Location spawnLoc = b.getLocation().add(0.5, 0, 0.5);
            BlockDisplay display = spawnLoc.getWorld().spawn(spawnLoc, BlockDisplay.class, bd -> {
                bd.setBlock(b.getBlockData());
                bd.setGravity(false);
            });
            cast.trackDisplay(display);
            displays.add(display);

            // Temporarily set block to air
            b.setType(Material.AIR, false);
        }

        // Lift & Hover phase (Ticks 0 - 30)
        cast.scheduleRepeatingTask(() -> {
            for (BlockDisplay bd : displays) {
                if (bd.isValid()) {
                    Location loc = bd.getLocation().add(0, 0.15, 0);
                    bd.teleport(loc);
                    plugin.getVisualAndSoundService().spawnDustParticle(loc, Color.fromRGB(139, 69, 19), 1.5f, 3);
                }
            }
            plugin.getVisualAndSoundService().playSound(playerLoc, "BLOCK_GRASS_BREAK", 0.8f, 0.8f);
        }, 0, 2, 30);

        // Launch towards enemies at tick 30
        cast.scheduleTask(() -> {
            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);

            for (BlockDisplay bd : displays) {
                if (bd.isValid()) {
                    Vector launchDir = dir.clone().multiply(1.5).setY(-0.2);
                    if (!targets.isEmpty()) {
                        LivingEntity target = targets.get(0);
                        launchDir = target.getLocation().toVector().subtract(bd.getLocation().toVector()).normalize().multiply(1.5);
                    }
                    bd.setVelocity(launchDir);
                }
            }

            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation();
                    tLoc.getWorld().spawnParticle(Particle.EXPLOSION, tLoc, 1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.2);
                    plugin.getVisualAndSoundService().playSound(tLoc, "ENTITY_GENERIC_EXPLODE", 1.0f, 0.8f);
                }
            } else {
                Location front = playerLoc.clone().add(dir.clone().multiply(5.0));
                front.getWorld().spawnParticle(Particle.EXPLOSION, front, 1);
                plugin.getDamageService().dealAoeDamage(player, front, config.getRadius(), config.getFallbackAoeDamage(), 1.0);
            }
        }, 30);

        // Restore blocks after 7 seconds (140 ticks)
        cast.scheduleTask(() -> {
            for (Map.Entry<Block, BlockState> entry : savedStates.entrySet()) {
                entry.getValue().update(true, false);
                entry.getKey().getWorld().spawnParticle(Particle.BLOCK, entry.getKey().getLocation().add(0.5, 0.5, 0.5), 15, 0.3, 0.3, 0.3, entry.getValue().getBlockData());
            }
            plugin.getVisualAndSoundService().playSound(playerLoc, "BLOCK_STONE_PLACE", 1.0f, 0.9f);
            cast.cleanup();
        }, 140);

        return true;
    }
}
