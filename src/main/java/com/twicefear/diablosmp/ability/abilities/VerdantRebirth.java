package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class VerdantRebirth extends Ability {

    public VerdantRebirth(DiabloSMP plugin) {
        super(plugin, "verdant_rebirth", "Verdant Rebirth",
              "Channel the forest's life force to heal yourself and allies.", "NATURE", 20);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.playEmote(player, "nature_kneel_anim");
        api.spawnParticleOnPlayer(player, "nature_aura_fx");
        api.runParticleEffectOnPlayer(player, "vine_growth");
        api.runParticleEffectOnPlayer(player, "leaf_bloom");
        api.runParticleEffectOnPlayer(player, "healing_pollen");

        sound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1.0f, 0.8f);
        sound(player.getLocation(), Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1.0f, 1.2f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

        BukkitRunnable heal = new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick >= 100) { cancel(); return; }
                if (tick % 20 == 0) {
                    for (LivingEntity e : getNearbyLivingEntities(player, 5.0)) {
                        if (!(e instanceof org.bukkit.entity.Monster)) {
                            e.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0));
                        }
                    }
                }
                tick++;
            }
        };
        heal.runTaskTimer(plugin, 0, 1);

        delay(120, () -> {
            api.runParticleEffectOnPlayer(player, "flower_burst");
            sound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.5f, 1.5f);

            Location loc = player.getLocation();
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    org.bukkit.block.Block b = loc.clone().add(x, 0, z).getBlock();
                    if (b.getType() == org.bukkit.Material.GRASS_BLOCK
                            && b.getRelative(0, 1, 0).getType() == org.bukkit.Material.AIR) {
                        if (Math.random() < 0.3) {
                            b.getRelative(0, 1, 0).setType(org.bukkit.Material.DANDELION);
                        }
                    }
                }
            }
            api.stopAllEmotes(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.GREEN
                    + "The forest blesses you.");
        });
    }
}
