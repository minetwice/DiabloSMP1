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

public class TempestStrike extends Ability {

    public TempestStrike(DiabloSMP plugin) {
        super(plugin, "tempest_strike", "Tempest Strike",
              "Dash forward as lightning, striking all in your path.", "LIGHTNING", 12);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "body", 1.3f);
        api.playEmote(player, "tempest_strike_anim");

        delay(10, () -> {
            api.runParticleEffectOnPlayer(player, "electric_burst");
            sound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.7f, 1.5f);

            Vector dir = player.getLocation().getDirection().normalize().multiply(8);
            Location dashTarget = player.getLocation().add(dir);

            api.runParticleEffectOnPlayer(player, "lightning_path");

            for (int i = 1; i <= 3; i++) {
                Location strikeLoc = player.getLocation().add(dir.clone().multiply(i / 3.0));
                delay((long) (i * 4), () -> {
                    strikeLoc.getWorld().strikeLightning(strikeLoc);
                    sound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.2f);
                    for (LivingEntity e : strikeLoc.getNearbyLivingEntities(2.0)) {
                        if (e != player) {
                            e.damage(12.0, player);
                            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 10));
                        }
                    }
                });
            }

            delay(12, () -> {
                player.teleport(dashTarget);
                api.runParticleEffectOnPlayer(player, "shockwave_ring");
                sound(dashTarget, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
            });
        });

        delay(60, () -> resetPlayer(player));
    }
}
