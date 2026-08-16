package com.diablosmp.plugin.service;

import com.diablosmp.plugin.DiabloSMP;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class VisualAndSoundService {
    private final DiabloSMP plugin;

    public VisualAndSoundService(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void playSound(org.bukkit.Location location, String soundName, float volume, float pitch) {
        if (location == null || soundName == null || soundName.isEmpty()) return;
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            location.getWorld().playSound(location, sound, volume, pitch);
        } catch (IllegalArgumentException ignored) {
            // Sound fallback if custom or invalid string
        }
    }

    public void spawnDustParticle(org.bukkit.Location location, Color color, float size, int count) {
        if (location == null) return;
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, size);
        location.getWorld().spawnParticle(Particle.DUST, location, count, 0.2, 0.2, 0.2, 0.05, dustOptions);
    }
}
