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

public class VoidCollapse extends Ability {

    public VoidCollapse(DiabloSMP plugin) {
        super(plugin, "void_collapse", "Void Collapse",
              "Implode into nothingness, then explode in a void burst.", "VOID", 25);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "body", 0.2f);
        api.setScale(player, "head", 0.2f);
        api.playEmote(player, "void_collapse_anim");
        api.runParticleEffectOnPlayer(player, "void_implosion");

        sound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1.0f, 0.3f);

        repeat(0, 2, 40, tick -> {
            for (LivingEntity e : getNearbyLivingEntities(player, 8.0)) {
                Vector pull = player.getLocation().toVector().subtract(e.getLocation().toVector());
                if (pull.length() > 0.5) {
                    pull.normalize().multiply(0.3);
                    e.setVelocity(pull);
                }
            }
        });

        delay(40, () -> {
            api.runParticleEffectOnPlayer(player, "void_explosion");
            api.spawnParticleOnPlayer(player, "void_darkness_fx");

            Location loc = player.getLocation();
            sound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.2f);
            sound(loc, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 0.3f);

            player.damage(8.0);

            for (LivingEntity e : getNearbyLivingEntities(player, 6.0)) {
                e.damage(24.0, player);
                e.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0));
            }
        });

        delay(60, () -> {
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.DARK_PURPLE
                    + "The void releases you.");
        });
    }
}
