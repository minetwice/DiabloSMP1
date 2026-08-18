package com.twicefear.diablosmp.ability.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.api.EmoteAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EtherealPhase extends Ability {

    public EtherealPhase(DiabloSMP plugin) {
        super(plugin, "ethereal_phase", "Ethereal Phase",
              "Phase into the spirit realm, becoming untouchable for a time.", "SPIRIT", 18);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "body", 0.4f);
        api.playEmote(player, "spirit_phase_anim");
        api.runParticleEffectOnPlayer(player, "ghost_trail");
        api.runParticleEffectOnPlayer(player, "soul_drift");
        api.runParticleEffectOnPlayer(player, "flicker_effect");

        sound(player.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 1.5f, 0.5f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 80, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 1));

        BukkitRunnable flicker = new BukkitRunnable() {
            int tick = 0;
            boolean invisible = false;
            @Override
            public void run() {
                if (tick >= 80) { cancel(); return; }
                if (tick % 10 == 0) {
                    invisible = !invisible;
                    if (invisible) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 12, 0));
                    }
                }
                tick++;
            }
        };
        flicker.runTaskTimer(plugin, 0, 1);

        delay(80, () -> {
            api.runParticleEffectOnPlayer(player, "ghost_burst");
            sound(player.getLocation(), Sound.ENTITY_ALLAY_DEATH, 1.5f, 0.8f);
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.GRAY + "You return to the mortal realm.");
        });
    }
}
