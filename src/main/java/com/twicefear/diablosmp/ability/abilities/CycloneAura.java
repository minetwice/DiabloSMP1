package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CycloneAura extends Ability {

    public CycloneAura(DiabloSMP plugin) {
        super(plugin, "cyclone_aura", "Cyclone Aura",
              "Levitate as the wind itself, repelling all who approach.", "WIND", 16);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.playEmote(player, "wind_levitate_anim");
        api.runParticleEffectOnPlayer(player, "cyclone_blades");
        api.runParticleEffectOnPlayer(player, "wind_current_down");
        api.spawnParticleOnPlayer(player, "wind_aura_fx");

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 0));

        repeat(0, 2, 20, tick -> player.setVelocity(player.getVelocity().setY(0.15)));

        sound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.5f);

        BukkitRunnable aura = new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick >= 80) { cancel(); return; }
                for (LivingEntity e : getNearbyLivingEntities(player, 4.0)) {
                    e.damage(4.0, player);
                    Vector knock = e.getLocation().toVector().subtract(player.getLocation().toVector());
                    if (knock.length() > 0) {
                        knock.normalize().multiply(1.5).setY(0.4);
                        e.setVelocity(knock);
                    }
                }
                tick += 10;
            }
        };
        aura.runTaskTimer(plugin, 0, 10);

        delay(40, () -> {
            api.runParticleEffectOnPlayer(player, "cyclone_release");
            sound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0f, 0.8f);
            for (LivingEntity e : getNearbyLivingEntities(player, 6.0)) {
                Vector knock = e.getLocation().toVector().subtract(player.getLocation().toVector());
                if (knock.length() > 0) {
                    knock.normalize().multiply(2.5).setY(0.6);
                    e.setVelocity(knock);
                }
            }
        });

        delay(100, () -> api.stopAllEmotes(player));
    }
}
