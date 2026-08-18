package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BloodCovenant extends Ability {

    public BloodCovenant(DiabloSMP plugin) {
        super(plugin, "blood_covenant", "Blood Covenant",
              "Empower your arm with blood magic, and steal life from your foes.", "BLOOD", 16);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "right_arm", 3.0f);
        api.playEmote(player, "blood_raise_anim");
        api.spawnParticleOnPlayer(player, "blood_aura_fx");
        api.runParticleEffectOnPlayer(player, "blood_spiral");
        api.runParticleEffectOnPlayer(player, "blood_drip");

        sound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.5f, 0.3f);
        player.damage(4.0);

        delay(30, () -> {
            api.runParticleEffectOnPlayer(player, "blood_slash");
            sound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2.0f, 0.4f);

            for (LivingEntity e : getNearbyLivingEntities(player, 4.0)) {
                e.damage(28.0, player);
                player.setHealth(Math.min(player.getHealth() + 14.0, player.getMaxHealth()));
                e.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.WITHER, 80, 1));
            }
        });

        delay(100, () -> {
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.DARK_RED
                    + "The blood pact fades.");
        });
    }
}
