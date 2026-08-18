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

public class ArcaneOverflow extends Ability {

    public ArcaneOverflow(DiabloSMP plugin) {
        super(plugin, "arcane_overflow", "Arcane Overflow",
              "Overflow with arcane knowledge, and unleash it in a devastating burst.", "ARCANE", 30);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "head", 2.5f);
        api.setScale(player, "body", 1.5f);
        api.playEmote(player, "arcane_channel_anim");

        api.runParticleEffectOnPlayer(player, "rune_circle");
        api.runParticleEffectOnPlayer(player, "runic_orbit");
        api.runParticleEffectOnPlayer(player, "arcane_stream_up");

        sound(player.getLocation(), Sound.BLOCK_BEACON_HUM, 2.0f, 0.5f);

        delay(60, () -> player.damage(6.0));

        delay(60, () -> {
            api.runParticleEffectOnPlayer(player, "arcane_explosion");
            Location loc = player.getLocation();
            sound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.4f);
            sound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 0.6f);

            for (LivingEntity e : getNearbyLivingEntities(player, 8.0)) {
                e.damage(30.0, player);
                e.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 0));
            }
        });

        delay(100, () -> {
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.AQUA
                    + "The arcane energies dissipate.");
        });
    }
}
