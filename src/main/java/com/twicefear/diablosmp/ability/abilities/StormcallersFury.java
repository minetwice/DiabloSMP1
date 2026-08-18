package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Random;

public class StormcallersFury extends Ability {

    public StormcallersFury(DiabloSMP plugin) {
        super(plugin, "stormcallers_fury", "Stormcaller's Fury",
              "Call down a storm of seven lightning bolts upon your enemies.", "STORM", 24);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "left_arm", 2.0f);
        api.setScale(player, "right_arm", 2.0f);
        api.playEmote(player, "storm_raise_anim");
        api.runParticleEffectOnPlayer(player, "storm_cloud");
        api.spawnParticleOnPlayer(player, "storm_spark_fx");

        sound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.5f);

        Random rand = new Random();
        Location center = player.getLocation();

        for (int i = 0; i < 7; i++) {
            delay((long) (20 + i * 15), () -> {
                double angle = rand.nextDouble() * Math.PI * 2;
                double dist = 2.0 + rand.nextDouble() * 6.0;
                Location strikeLoc = center.clone().add(Math.cos(angle) * dist, 0, Math.sin(angle) * dist);

                strikeLoc.getWorld().strikeLightning(strikeLoc);
                sound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.5f);
                api.spawnParticleOnPlayer(player, "storm_spark_fx");

                for (LivingEntity e : strikeLoc.getNearbyLivingEntities(2.0)) {
                    if (e != player) e.damage(8.0, player);
                }
            });
        }

        delay(100, () -> {
            api.runParticleEffectOnPlayer(player, "final_lightning");
            center.getWorld().strikeLightning(center);
            sound(center, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 0.3f);
            for (LivingEntity e : getNearbyLivingEntities(player, 5.0)) {
                e.damage(16.0, player);
            }
        });

        delay(120, () -> {
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.YELLOW + "The storm subsides.");
        });
    }
}
