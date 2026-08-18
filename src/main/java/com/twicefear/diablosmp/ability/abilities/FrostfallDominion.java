package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FrostfallDominion extends Ability {

    public FrostfallDominion(DiabloSMP plugin) {
        super(plugin, "frostfall_dominion", "Frostfall Dominion",
              "Freeze the world around you in a prison of ice.", "ICE", 18);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.playEmote(player, "frost_channel_anim");
        api.spawnParticleOnPlayer(player, "frost_aura_fx");
        api.runParticleEffectOnPlayer(player, "frost_rings");
        api.runParticleEffectOnPlayer(player, "snowflake_spiral");

        sound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.8f, 0.5f);

        delay(40, () -> {
            api.runParticleEffectOnPlayer(player, "frost_breath");
            Location loc = player.getLocation();
            sound(loc, Sound.BLOCK_GLASS_PLACE, 1.0f, 0.4f);

            for (LivingEntity e : getNearbyLivingEntities(player, 6.0)) {
                e.damage(6.0, player);
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 3));
            }

            for (int x = -6; x <= 6; x++) {
                for (int z = -6; z <= 6; z++) {
                    Block b = loc.clone().add(x, 0, z).getBlock();
                    if (b.getType() == Material.WATER) {
                        Block finalBlock = b;
                        Bukkit.getScheduler().runTask(plugin, () -> finalBlock.setType(Material.ICE));
                        delay(200, () -> {
                            if (finalBlock.getType() == Material.ICE)
                                finalBlock.setType(Material.WATER);
                        });
                    }
                }
            }
        });

        delay(100, () -> {
            api.stopAllEmotes(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.AQUA
                    + "Frostfall Dominion ended.");
        });
    }
}
