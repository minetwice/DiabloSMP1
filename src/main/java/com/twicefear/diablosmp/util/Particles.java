package com.twicefear.diablosmp.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Random;

public final class Particles {

    private Particles() {}
    private static final Random RAND = new Random();

    public static void ring(Location center, double radius, Particle particle, Color color, int count) {
        World world = center.getWorld();
        if (world == null) return;
        Particle.DustOptions dust = color != null ? new Particle.DustOptions(color, 1.4f) : null;
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 * i) / count;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Location loc = center.clone().add(x, 0, z);
            if (dust != null) world.spawnParticle(particle, loc, 0, 0, 0, 0, dust);
            else world.spawnParticle(particle, loc, 1);
        }
    }

    public static void sphere(Location center, double radius, Particle particle, Color color, int count) {
        World world = center.getWorld();
        if (world == null) return;
        Particle.DustOptions dust = color != null ? new Particle.DustOptions(color, 1.0f) : null;
        for (int i = 0; i < count; i++) {
            double theta = RAND.nextDouble() * Math.PI * 2;
            double phi = Math.acos(RAND.nextDouble() * 2 - 1);
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.cos(phi);
            double z = radius * Math.sin(phi) * Math.sin(theta);
            Location loc = center.clone().add(x, y, z);
            if (dust != null) world.spawnParticle(particle, loc, 0, 0, 0, 0, dust);
            else world.spawnParticle(particle, loc, 1);
        }
    }

    public static void spiral(Location center, double radius, double height, int turns,
                              Particle particle, Color color, int points) {
        World world = center.getWorld();
        if (world == null) return;
        Particle.DustOptions dust = color != null ? new Particle.DustOptions(color, 1.2f) : null;
        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;
            double angle = turns * Math.PI * 2 * t;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = height * t;
            Location loc = center.clone().add(x, y, z);
            if (dust != null) world.spawnParticle(particle, loc, 0, 0, 0, 0, dust);
            else world.spawnParticle(particle, loc, 1);
        }
    }

    public static void line(Location from, Location to, Particle particle, Color color, int count) {
        World world = from.getWorld();
        if (world == null) return;
        Particle.DustOptions dust = color != null ? new Particle.DustOptions(color, 1.2f) : null;
        Vector dir = to.toVector().subtract(from.toVector());
        double length = dir.length();
        dir.normalize();
        for (int i = 0; i <= count; i++) {
            double t = (double) i / count;
            Location loc = from.clone().add(dir.clone().multiply(length * t));
            if (dust != null) world.spawnParticle(particle, loc, 0, 0, 0, 0, dust);
            else world.spawnParticle(particle, loc, 1);
        }
    }

    public static void burst(Location center, Particle particle, Color color, int count, double speed) {
        World world = center.getWorld();
        if (world == null) return;
        Particle.DustOptions dust = color != null ? new Particle.DustOptions(color, 1.6f) : null;
        if (dust != null) world.spawnParticle(particle, center, count, 0.3, 0.3, 0.3, speed, dust);
        else world.spawnParticle(particle, center, count, 0.3, 0.3, 0.3, speed);
    }

    public static void doubleHelix(Location center, double radius, double height, int turns,
                                   Particle particle, Color color, int points) {
        World world = center.getWorld();
        if (world == null) return;
        Particle.DustOptions dust = color != null ? new Particle.DustOptions(color, 1.0f) : null;
        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;
            double angle = turns * Math.PI * 2 * t;
            double x1 = Math.cos(angle) * radius;
            double z1 = Math.sin(angle) * radius;
            double x2 = Math.cos(angle + Math.PI) * radius;
            double z2 = Math.sin(angle + Math.PI) * radius;
            double y = height * t;
            Location a = center.clone().add(x1, y, z1);
            Location b = center.clone().add(x2, y, z2);
            if (dust != null) {
                world.spawnParticle(particle, a, 0, 0, 0, 0, dust);
                world.spawnParticle(particle, b, 0, 0, 0, 0, dust);
            } else {
                world.spawnParticle(particle, a, 1);
                world.spawnParticle(particle, b, 1);
            }
        }
    }

    public static void orbit(Location center, double radius, double yOffset, double angle,
                             Particle particle, Color color, int points) {
        World world = center.getWorld();
        if (world == null) return;
        Particle.DustOptions dust = color != null ? new Particle.DustOptions(color, 1.1f) : null;
        for (int i = 0; i < points; i++) {
            double a = angle + (Math.PI * 2 * i) / points;
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            Location loc = center.clone().add(x, yOffset, z);
            if (dust != null) world.spawnParticle(particle, loc, 0, 0, 0, 0, dust);
            else world.spawnParticle(particle, loc, 1);
        }
    }
}
