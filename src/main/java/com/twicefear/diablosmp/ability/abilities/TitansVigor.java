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

public class TitansVigor extends Ability {

    public TitansVigor(DiabloSMP plugin) {
        super(plugin, "titans_vigor", "Titan's Vigor",
              "Become the mountain itself, and shatter the earth with your fist.", "EARTH", 25);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "body", 3.0f);
        api.setScale(player, "head", 2.2f);
        api.setScale(player, "arms", 2.5f);
        api.playEmote(player, "titan_raise_anim");

        sound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.0f, 0.4f);

        delay(30, () -> {
            api.spawnParticleOnPlayer(player, "earth_dust_fx");
            sound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 1.5f, 0.3f);

            delay(10, () -> {
                api.playEmote(player, "titan_slam_anim");
                api.runParticleEffectOnPlayer(player, "ground_crack");
                api.runParticleEffectOnPlayer(player, "shockwave_stone");

                Location loc = player.getLocation();
                sound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.3f);
                sound(loc, Sound.BLOCK_STONE_BREAK, 1.5f, 0.3f);

                for (LivingEntity e : getNearbyLivingEntities(player, 8.0)) {
                    e.damage(20.0, player);
                    e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                }
            });
        });

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 160, 2));

        delay(160, () -> {
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.GRAY + "Titan form ended.");
        });
    }
}
