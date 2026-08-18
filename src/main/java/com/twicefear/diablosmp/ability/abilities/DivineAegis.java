package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DivineAegis extends Ability {

    public DivineAegis(DiabloSMP plugin) {
        super(plugin, "divine_aegis", "Divine Aegis",
              "Kneel in prayer, and let the light protect you and your allies.", "HOLY", 22);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "body", 0.5f);
        api.setScale(player, "head", 0.6f);
        api.playEmote(player, "holy_kneel_anim");
        api.spawnParticleOnPlayer(player, "golden_light_fx");
        api.runParticleEffectOnPlayer(player, "aegis_dome");
        api.runParticleEffectOnPlayer(player, "heavenly_rays");

        sound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.5f, 1.2f);

        BukkitRunnable pulse = new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick >= 140) { cancel(); return; }
                if (tick % 20 == 0) {
                    api.runParticleEffectOnPlayer(player, "healing_pulse");
                    sound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8f, 1.5f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                    for (LivingEntity e : getNearbyLivingEntities(player, 4.0)) {
                        if (!(e instanceof Monster))
                            e.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                    }
                }
                tick++;
            }
        };
        pulse.runTaskTimer(plugin, 0, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 140, 3));

        delay(140, () -> {
            api.runParticleEffectOnPlayer(player, "dome_shatter");
            sound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.5f, 0.8f);
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.YELLOW + "Divine Aegis faded.");
        });
    }
}
