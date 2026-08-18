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

public class GravityWell extends Ability {

    public GravityWell(DiabloSMP plugin) {
        super(plugin, "gravity_well", "Gravity Well",
              "Bend gravity to your will, crushing all who dare approach.", "GRAVITY", 22);
    }

    @Override
    public void onCast(Player player) {
        EmoteAPI api = AethelionHook.get();
        castMessage(player);

        api.setScale(player, "left_arm", 2.8f);
        api.setScale(player, "right_arm", 2.8f);
        api.playEmote(player, "gravity_pull_anim");
        api.runParticleEffectOnPlayer(player, "gravity_orbit");
        api.runParticleEffectOnPlayer(player, "debris_field");
        api.runParticleEffectOnPlayer(player, "gravity_ring_contract");

        sound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.5f, 0.3f);

        repeat(0, 2, 40, tick -> {
            for (LivingEntity e : getNearbyLivingEntities(player, 8.0)) {
                Vector pull = player.getLocation().toVector().subtract(e.getLocation().toVector());
                if (pull.length() > 1.0) {
                    pull.normalize().multiply(0.25);
                    e.setVelocity(pull);
                }
            }
        });

        delay(40, () -> {
            api.playEmote(player, "gravity_slam_anim");
            api.runParticleEffectOnPlayer(player, "debris_crash");
            api.runParticleEffectOnPlayer(player, "heavy_shockwave");

            Location loc = player.getLocation();
            sound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.2f);
            sound(loc, Sound.ENTITY_WARDEN_DEATH, 1.0f, 0.5f);

            for (LivingEntity e : getNearbyLivingEntities(player, 5.0)) {
                e.damage(20.0, player);
                e.setVelocity(e.getVelocity().setY(1.0));
                e.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 0));
            }

            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    org.bukkit.block.Block b = loc.clone().add(x, -1, z).getBlock();
                    if (b.getType() == org.bukkit.Material.STONE
                            || b.getType() == org.bukkit.Material.DIRT
                            || b.getType() == org.bukkit.Material.GRASS_BLOCK) {
                        b.setType(org.bukkit.Material.AIR);
                    }
                }
            }
        });

        delay(100, () -> {
            resetPlayer(player);
            player.sendMessage(plugin.prefix() + net.md_5.bungee.api.ChatColor.GRAY + "Gravity returns to normal.");
        });
    }
}
