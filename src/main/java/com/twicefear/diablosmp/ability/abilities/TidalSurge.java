package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TidalSurge extends Ability {

    public TidalSurge(DiabloSMP plugin) {
        super(plugin, "tidal_surge", "Tidal Surge",
              "Call forth a wave that washes away all before you.", "WATER", 14);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.playEmote(player, "water_sweep_anim");
        api.spawnParticleOnPlayer(player, "water_aura_fx");
        api.runParticleEffectOnPlayer(player, "water_spiral");

        sound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.5f, 0.6f);

        delay(40, () -> {
            api.runParticleEffectOnPlayer(player, "tidal_wave");
            api.runParticleEffectOnPlayer(player, "bubble_trail");

            Location loc = player.getLocation();
            Vector dir = loc.getDirection().normalize();
            sound(loc, Sound.ENTITY_GENERIC_SPLASH, 2.0f, 0.5f);

            for (int step = 1; step <= 10; step++) {
                final int s = step;
                delay((long) (step * 2), () -> {
                    Location waveLoc = loc.clone().add(dir.clone().multiply(s));
                    for (LivingEntity e : waveLoc.getNearbyLivingEntities(2.0)) {
                        if (e != player) {
                            e.damage(10.0, player);
                            e.setVelocity(dir.clone().multiply(2.0).setY(0.3));
                        }
                    }
                    waveLoc.getWorld().spawnParticle(Particle.SPLASH, waveLoc, 20, 1, 1, 1, 0.1);
                    if (waveLoc.getBlock().getType() == org.bukkit.Material.FIRE)
                        waveLoc.getBlock().setType(org.bukkit.Material.AIR);
                });
            }
        });

        delay(80, () -> api.stopAllEmotes(player));
    }
}
