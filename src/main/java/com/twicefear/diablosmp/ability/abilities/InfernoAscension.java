package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class InfernoAscension extends Ability {

    public InfernoAscension(DiabloSMP plugin) {
        super(plugin, "inferno_ascension", "Inferno Ascension",
              "Rise as a titan of flame, calling meteors from the sky.", "FIRE", 20);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.playEmote(player, "inferno_channel_anim");
        api.setScale(player, "body", 2.2f);
        api.setScale(player, "head", 1.8f);
        api.spawnParticleOnPlayer(player, "fire_aura_fx");
        api.runParticleEffectOnPlayer(player, "inferno_spiral");

        sound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.5f, 0.5f);

        delay(60, () -> {
            api.runParticleEffectOnPlayer(player, "meteor_rain");
            sound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.6f);
            damageNearby(player, 4.0, 8.0, true);
            for (LivingEntity e : getNearbyLivingEntities(player, 4.0)) {
                e.setFireTicks(100);
            }
        });

        delay(120, () -> {
            api.runParticleEffectOnPlayer(player, "meteor_rain");
            sound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 0.7f);
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.YELLOW
                    + "Inferno Ascension ended.");
        });
    }
}
