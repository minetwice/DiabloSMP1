package com.twicefear.diablosmp.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.InputStream;

/**
 * Interface mirroring Aethelion's EmoteAPI.
 * All methods delegate to the Aethelion plugin at runtime.
 */
public interface EmoteAPI {

    // ── Animation ────────────────────────────────────────────
    void registerAnimation(String id, InputStream stream);
    void playEmote(Player player, String animationId);
    void stopEmote(Player player, String animationId);
    void stopAllEmotes(Player player);

    // ── Scale ────────────────────────────────────────────────
    void setScale(Player player, String bone, float scale);
    void resetScale(Player player, String bone);

    // ── JSON Particle ────────────────────────────────────────
    void registerParticle(String id, InputStream stream);
    void spawnParticleOnPlayer(Player player, String particleId);

    // ── Procedural Code Particle ─────────────────────────────
    void registerCustomCodeParticleEffect(String id, int durationTicks, ParticleCallback callback);
    void runParticleEffectOnPlayer(Player player, String effectId);

    @FunctionalInterface
    interface ParticleCallback {
        void tick(Location origin, int tick, Object effect);
    }
}
