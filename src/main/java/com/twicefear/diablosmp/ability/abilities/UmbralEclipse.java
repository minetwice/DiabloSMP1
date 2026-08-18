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
import org.bukkit.util.Vector;

import java.util.List;

public class UmbralEclipse extends Ability {

    public UmbralEclipse(DiabloSMP plugin) {
        super(plugin, "umbral_eclipse", "Umbral Eclipse",
              "Sink into shadow and strike from behind your foe.", "SHADOW", 18);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "body", 0.3f);
        api.playEmote(player, "shadow_embrace_anim");
        api.spawnParticleOnPlayer(player, "shadow_burst_fx");
        api.runParticleEffectOnPlayer(player, "shadow_tendrils");

        sound(player.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 1.0f, 0.5f);

        delay(20, () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0));

            delay(40, () -> {
                List<LivingEntity> nearby = getNearbyLivingEntities(player, 15.0);
                if (nearby.isEmpty()) {
                    Vector dir = player.getLocation().getDirection().normalize().multiply(10);
                    Location dest = player.getLocation().add(dir);
                    player.teleport(dest);
                } else {
                    LivingEntity target = nearby.get(0);
                    Vector behind = target.getLocation().getDirection().normalize().multiply(-1.5);
                    Location dest = target.getLocation().add(behind);
                    dest.setY(target.getLocation().getY());
                    player.teleport(dest);
                    target.damage(24.0, player);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 1));
                }

                api.runParticleEffectOnPlayer(player, "dark_trail");
                api.spawnParticleOnPlayer(player, "shadow_reappear_fx");
                sound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.6f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
            });
        });

        delay(80, () -> {
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.DARK_PURPLE
                    + "You emerge from the shadows.");
        });
    }
}
