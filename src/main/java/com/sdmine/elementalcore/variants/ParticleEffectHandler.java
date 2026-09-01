package com.sdmine.elementalcore.variants;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

public class ParticleEffectHandler {
    public void emitParticles(Player player, WeaponVariant variant) {
        if (variant == null) return;
        String type = variant.getParticleType();
        if ("NONE".equals(type)) return;
        Location loc = player.getLocation().add(0, 1, 0);
        switch (type) {
            case "FLAME" -> loc.getWorld().spawnParticle(Particle.FLAME, loc, 3, 0.3, 0.5, 0.3, 0.02);
            case "CAMPFIRE_SMOKE_WATER" -> { loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 2, 0.3, 0.5, 0.3, 0.01); loc.getWorld().spawnParticle(Particle.SPLASH, loc, 3, 0.3, 0.5, 0.3, 0.1); }
            case "WATER_DRIP_LAVA_SPARK" -> { loc.getWorld().spawnParticle(Particle.DRIPPING_WATER, loc, 2, 0.3, 0.5, 0.3, 0); loc.getWorld().spawnParticle(Particle.LAVA, loc, 1, 0.3, 0.5, 0.3, 0); }
            case "WATER_SPLASH_BUBBLE" -> { loc.getWorld().spawnParticle(Particle.SPLASH, loc, 3, 0.3, 0.5, 0.3, 0.1); loc.getWorld().spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0,-0.5,0), 2, 0.3, 0.3, 0.3, 0); }
            case "FLAME_SWIRL_CLOUD" -> { loc.getWorld().spawnParticle(Particle.FLAME, loc, 2, 0.3, 0.5, 0.3, 0.02); loc.getWorld().spawnParticle(Particle.CLOUD, loc, 2, 0.3, 0.5, 0.3, 0.01); }
            case "SNOWFLAKE_CYAN" -> { loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 3, 0.3, 0.5, 0.3, 0.01); loc.getWorld().spawnParticle(Particle.INSTANT_EFFECT, loc, 2, 0.3, 0.5, 0.3, 0); }
            case "GREEN_SPARK_CRACK" -> { loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 3, 0.3, 0.5, 0.3, 0); loc.getWorld().spawnParticle(Particle.BLOCK, loc, 2, 0.3, 0.5, 0.3, 0, org.bukkit.Material.DIRT.createBlockData()); }
            case "LAVA_POP_CRIT" -> { loc.getWorld().spawnParticle(Particle.LAVA, loc, 2, 0.3, 0.5, 0.3, 0); loc.getWorld().spawnParticle(Particle.CRIT, loc, 2, 0.3, 0.5, 0.3, 0); }
            case "DRAGON_BREATH_PORTAL" -> { loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 2, 0.3, 0.5, 0.3, 0.01); loc.getWorld().spawnParticle(Particle.PORTAL, loc, 3, 0.3, 0.5, 0.3, 0.5); }
            case "SWEEP_CLOUD" -> { loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0.3, 0.5, 0.3, 0); loc.getWorld().spawnParticle(Particle.CLOUD, loc, 2, 0.3, 0.5, 0.3, 0.01); }
            case "END_ROD_GLOWSTONE" -> { loc.getWorld().spawnParticle(Particle.END_ROD, loc, 3, 0.3, 0.5, 0.3, 0.02); loc.getWorld().spawnParticle(Particle.MYCELIUM, loc, 2, 0.3, 0.5, 0.3, 0); }
            case "TRI_COLOR_BURST" -> { DustOptions r = new DustOptions(Color.RED, 1.2f), b = new DustOptions(Color.BLUE, 1.2f), y = new DustOptions(Color.YELLOW, 1.2f); loc.getWorld().spawnParticle(Particle.DUST, loc.clone().add(0.2,0,0), 1, 0,0,0,0, r); loc.getWorld().spawnParticle(Particle.DUST, loc.clone().add(-0.2,0,0), 1, 0,0,0,0, b); loc.getWorld().spawnParticle(Particle.DUST, loc.clone().add(0,0.2,0), 1, 0,0,0,0, y); }
            default -> {}
        }
    }
}
