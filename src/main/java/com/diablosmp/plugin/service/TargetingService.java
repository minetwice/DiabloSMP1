package com.diablosmp.plugin.service;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.TargetingShape;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TargetingService {
    private final DiabloSMP plugin;

    public TargetingService(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public List<LivingEntity> getTargets(Player caster, DiabloStoneType type) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(type);
        if (config == null) return new ArrayList<>();

        double radius = config.getRadius();
        int maxTargets = config.getMaxTargets();
        TargetingShape shape = config.getTargetingShape();
        double angle = config.getAngle();

        return getTargetsCustom(caster, shape, radius, angle, maxTargets);
    }

    public List<LivingEntity> getTargetsCustom(Player caster, TargetingShape shape, double radius, double angle, int maxTargets) {
        List<LivingEntity> targets = new ArrayList<>();
        Location casterLoc = caster.getLocation();

        boolean targetPlayers = plugin.getConfig().getBoolean("targeting.players", true);
        boolean targetHostile = plugin.getConfig().getBoolean("targeting.hostile-mobs", true);
        boolean ignoreCreative = plugin.getConfig().getBoolean("targeting.ignore-creative", true);
        boolean ignoreSpectator = plugin.getConfig().getBoolean("targeting.ignore-spectator", true);
        boolean friendlyFire = plugin.getConfig().getBoolean("targeting.friendly-fire", false);

        int hardCap = plugin.getConfig().getInt("targeting.max-targets-hard-cap", 60);
        int effectiveMax = Math.min(maxTargets, hardCap);

        for (LivingEntity entity : caster.getWorld().getLivingEntities()) {
            if (entity.equals(caster)) continue;
            if (entity.isDead() || !entity.isValid()) continue;

            if (entity instanceof ArmorStand && !plugin.getConfig().getBoolean("targeting.armor-stands", false)) {
                continue;
            }

            if (entity instanceof Player p) {
                if (!targetPlayers) continue;
                if (ignoreCreative && p.getGameMode() == org.bukkit.GameMode.CREATIVE) continue;
                if (ignoreSpectator && p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
                if (!friendlyFire && isTeammate(caster, p)) continue;
            } else if (!targetHostile) {
                continue;
            }

            Location entityLoc = entity.getLocation();
            double distanceSq = casterLoc.distanceSquared(entityLoc);
            if (distanceSq > radius * radius) continue;

            boolean validShape = switch (shape) {
                case RADIUS -> true;
                case CONE -> isInCone(casterLoc, entityLoc, angle);
                case LINE -> isInLine(casterLoc, entityLoc, radius, 2.0);
                case FORWARD_BOX -> isInForwardBox(casterLoc, entityLoc, radius, 3.0);
                case SPECIAL -> true;
            };

            if (validShape) {
                targets.add(entity);
            }
        }

        targets.sort(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(casterLoc)));

        if (targets.size() > effectiveMax) {
            return new ArrayList<>(targets.subList(0, effectiveMax));
        }

        return targets;
    }

    private boolean isInCone(Location source, Location target, double angleDegrees) {
        Vector dir = source.getDirection().normalize();
        Vector toTarget = target.toVector().subtract(source.toVector()).normalize();
        double dot = dir.dot(toTarget);
        double angleToTarget = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dot))));
        return angleToTarget <= (angleDegrees / 2.0);
    }

    private boolean isInLine(Location source, Location target, double length, double width) {
        Vector dir = source.getDirection().normalize();
        Vector toTarget = target.toVector().subtract(source.toVector());
        double dot = toTarget.dot(dir);
        if (dot < 0 || dot > length) return false;
        Vector projected = dir.clone().multiply(dot);
        double perpDistSq = toTarget.subtract(projected).lengthSquared();
        return perpDistSq <= (width / 2.0) * (width / 2.0);
    }

    private boolean isInForwardBox(Location source, Location target, double forwardDepth, double boxWidth) {
        Vector dir = source.getDirection().setY(0).normalize();
        Vector toTarget = target.toVector().subtract(source.toVector());
        toTarget.setY(0);
        double dot = toTarget.dot(dir);
        if (dot < 0 || dot > forwardDepth) return false;
        Vector projected = dir.clone().multiply(dot);
        double sideDist = toTarget.subtract(projected).length();
        return sideDist <= (boxWidth / 2.0);
    }

    private boolean isTeammate(Player p1, Player p2) {
        if (p1.getScoreboard().getPlayerTeam(p1) != null && p2.getScoreboard().getPlayerTeam(p2) != null) {
            return p1.getScoreboard().getPlayerTeam(p1).equals(p2.getScoreboard().getPlayerTeam(p2));
        }
        return false;
    }
}
