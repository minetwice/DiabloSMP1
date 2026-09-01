package com.sdmine.elementalcore.variants;

import com.sdmine.elementalcore.ElementalCorePlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityExecutor {
    private final ElementalCorePlugin plugin;
    private final Map<UUID, Map<String, Long>> cooldowns;

    public AbilityExecutor(ElementalCorePlugin plugin) { this.plugin = plugin; this.cooldowns = new ConcurrentHashMap<>(); }

    public boolean execute(WeaponVariant variant, Player player) {
        if (variant == null || !variant.hasActiveAbility()) return false;
        String type = variant.getActiveType();
        int cd = variant.getCooldown();
        if (isOnCooldown(player.getUniqueId(), type, cd)) { player.sendMessage("§7§l[ECW] §cCooldown: " + getRemaining(player.getUniqueId(), type) + "s"); return false; }
        setCooldown(player.getUniqueId(), type, cd);
        playSound(player, variant.getSound());
        Map<String, Object> cfg = variant.getActiveConfig();
        switch (type) {
            case "IGNITION_BURST" -> ignitionBurst(player, cfg);
            case "SCALDING_ERUPTION" -> scaldingEruption(player, cfg);
            case "GEYSER_LAUNCH" -> geyserLaunch(player, cfg);
            case "TSUNAMI_WAVE" -> tsunamiWave(player, cfg);
            case "FIRE_TORNADO" -> fireTornado(player, cfg);
            case "ABSOLUTE_ZERO" -> absoluteZero(player, cfg);
            case "EARTHQUAKE_BARRIER" -> earthquakeBarrier(player, cfg);
            case "VOLCANIC_PILLAR" -> volcanicPillar(player, cfg);
            case "SHADOW_STEP_STRIKE" -> shadowStep(player, cfg);
            case "AIR_CUT_DASH" -> airCutDash(player, cfg);
            case "HOLY_BEAM" -> holyBeam(player, cfg);
            case "ELEMENTAL_OVERLOAD" -> elementalOverload(player, cfg);
            default -> { return false; }
        }
        return true;
    }

    private boolean isOnCooldown(UUID id, String a, int cd) { Map<String,Long> m = cooldowns.get(id); return m != null && m.get(a) != null && System.currentTimeMillis() < m.get(a); }
    private void setCooldown(UUID id, String a, int cd) { if (cd > 0) cooldowns.computeIfAbsent(id, k -> new HashMap<>()).put(a, System.currentTimeMillis() + cd * 1000L); }
    private long getRemaining(UUID id, String a) { Map<String,Long> m = cooldowns.get(id); if (m == null || m.get(a) == null) return 0; return Math.max(0, (m.get(a) - System.currentTimeMillis()) / 1000); }

    private void ignitionBurst(Player p, Map<String,Object> c) {
        Location l = p.getLocation(); double r = toD(c.getOrDefault("radius",5)), dmg = toD(c.getOrDefault("damage",8)); boolean fire = toB(c.getOrDefault("light_fire",true));
        for (Entity e : p.getWorld().getNearbyEntities(l, r, r, r)) if (e instanceof LivingEntity && !e.equals(p)) ((LivingEntity)e).damage(dmg, p);
        if (fire) for (int x = -(int)r; x <= (int)r; x++) for (int z = -(int)r; z <= (int)r; z++) if (x*x+z*z <= r*r) { Block b = l.clone().add(x,-1,z).getBlock(), a = l.clone().add(x,0,z).getBlock(); if (b.getType().isSolid() && a.getType() == Material.AIR) a.setType(Material.FIRE); }
        p.getWorld().createExplosion(l, 0f, false, false);
    }

    private void scaldingEruption(Player p, Map<String,Object> c) {
        Location l = p.getLocation(); boolean kb = toB(c.getOrDefault("knockback",true)); int dur = toI(c.getOrDefault("armor_reduction_duration",8));
        for (Entity e : p.getWorld().getNearbyEntities(l, 6, 6, 6)) if (e instanceof LivingEntity && !e.equals(p)) { LivingEntity t = (LivingEntity)e; if (kb) { Vector d = t.getLocation().toVector().subtract(l.toVector()).normalize(); t.setVelocity(d.multiply(2.0).setY(0.5)); } t.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, dur*20, 0)); }
    }

    private void geyserLaunch(Player p, Map<String,Object> c) {
        double dmg = toD(c.getOrDefault("damage",10)); boolean up = toB(c.getOrDefault("launch_up",true)); Location eye = p.getEyeLocation(); Vector dir = eye.getDirection();
        RayTraceResult r = p.getWorld().rayTraceEntities(eye, dir, 15, 0.5, e -> e instanceof LivingEntity && !e.equals(p));
        if (r != null && r.getHitEntity() instanceof LivingEntity) { LivingEntity t = (LivingEntity)r.getHitEntity(); t.damage(dmg, p); if (up) t.setVelocity(new Vector(0, 2, 0)); }
    }

    private void tsunamiWave(Player p, Map<String,Object> c) {
        Location l = p.getLocation(); double r = toD(c.getOrDefault("radius",7)), kv = toD(c.getOrDefault("knockback_velocity",1.5));
        for (Entity e : p.getWorld().getNearbyEntities(l, r, r, r)) if (e instanceof LivingEntity && !e.equals(p)) { Vector d = e.getLocation().toVector().subtract(l.toVector()).normalize(); e.setVelocity(d.multiply(kv*2).setY(0.8)); }
    }

    private void fireTornado(Player p, Map<String,Object> c) {
        Location l = p.getLocation(); int dur = toI(c.getOrDefault("duration",4)); double pr = toD(c.getOrDefault("pull_radius",6)), dmg = toD(c.getOrDefault("damage",5));
        new BukkitRunnable() { int t = 0; public void run() { if (t >= dur*20) { cancel(); return; } for (Entity e : p.getWorld().getNearbyEntities(l, pr, pr, pr)) if (e instanceof LivingEntity && !e.equals(p)) { LivingEntity le = (LivingEntity)e; le.setVelocity(l.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.3)); le.damage(dmg/(dur*10.0), p); le.setFireTicks(20); } t += 10; } }.runTaskTimer(plugin, 0L, 10L);
    }

    private void absoluteZero(Player p, Map<String,Object> c) {
        int dur = toI(c.getOrDefault("freeze_duration",3)); Location eye = p.getEyeLocation(); Vector dir = eye.getDirection();
        RayTraceResult r = p.getWorld().rayTraceEntities(eye, dir, 20, 0.5, e -> e instanceof LivingEntity && !e.equals(p));
        if (r == null || !(r.getHitEntity() instanceof LivingEntity)) { p.sendMessage("§7§l[ECW] §cNo target."); return; }
        LivingEntity t = (LivingEntity)r.getHitEntity(); Location tl = t.getLocation(); List<Block> ice = new ArrayList<>();
        for (int x=-1;x<=1;x++) for (int y=0;y<=2;y++) for (int z=-1;z<=1;z++) { Block b = tl.clone().add(x,y,z).getBlock(); if (b.getType()==Material.AIR) { b.setType(Material.BLUE_ICE); ice.add(b); } }
        t.setVelocity(new Vector(0,0,0)); t.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, dur*20, 255));
        new BukkitRunnable() { public void run() { for (Block b : ice) if (b.getType()==Material.BLUE_ICE) b.setType(Material.AIR); } }.runTaskLater(plugin, dur*20L);
    }

    private void earthquakeBarrier(Player p, Map<String,Object> c) {
        int dur = toI(c.getOrDefault("invincibility_duration",4)); boolean raise = toB(c.getOrDefault("raise_blocks",true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, dur*20, 255));
        if (raise) { Location l = p.getLocation(); int r = 3; for (int x=-r;x<=r;x++) for (int z=-r;z<=r;z++) { if (x==0&&z==0) continue; if (x*x+z*z<=r*r) { Block b = l.clone().add(x,0,z).getBlock(); if (b.getType()==Material.AIR) b.setType(Material.COBBLESTONE); } } }
    }

    private void volcanicPillar(Player p, Map<String,Object> c) {
        double dmg = toD(c.getOrDefault("damage",12)); boolean up = toB(c.getOrDefault("launch_up",true)); boolean fire = toB(c.getOrDefault("set_on_fire",true)); int fd = toI(c.getOrDefault("fire_duration",5));
        Location eye = p.getEyeLocation(); Vector dir = eye.getDirection();
        RayTraceResult r = p.getWorld().rayTraceEntities(eye, dir, 20, 0.5, e -> e instanceof LivingEntity && !e.equals(p));
        if (r == null || !(r.getHitEntity() instanceof LivingEntity)) { p.sendMessage("§7§l[ECW] §cNo target."); return; }
        LivingEntity t = (LivingEntity)r.getHitEntity(); t.damage(dmg, p); if (up) t.setVelocity(new Vector(0,2.5,0)); if (fire) t.setFireTicks(fd*20);
    }

    private void shadowStep(Player p, Map<String,Object> c) {
        int wl = toI(c.getOrDefault("wither_level",2))-1, wd = toI(c.getOrDefault("wither_duration",5))*20;
        Location eye = p.getEyeLocation(); Vector dir = eye.getDirection();
        RayTraceResult r = p.getWorld().rayTraceEntities(eye, dir, 25, 0.5, e -> e instanceof LivingEntity && !e.equals(p));
        if (r == null || !(r.getHitEntity() instanceof LivingEntity)) { p.sendMessage("§7§l[ECW] §cNo target."); return; }
        LivingEntity t = (LivingEntity)r.getHitEntity(); Location tl = t.getLocation(); Vector f = tl.getDirection();
        p.teleport(tl.clone().subtract(f.clone().multiply(1.5)).setY(tl.getY()));
        t.damage(15.0, p); t.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, wd, wl));
    }

    private void airCutDash(Player p, Map<String,Object> c) {
        int dist = toI(c.getOrDefault("dash_distance",10)); double dmg = toD(c.getOrDefault("damage",7));
        Location l = p.getLocation(); Vector dir = l.getDirection().setY(0).normalize();
        for (Entity e : p.getWorld().getNearbyEntities(l, dist, 3, dist)) if (e instanceof LivingEntity && !e.equals(p)) { Vector to = e.getLocation().toVector().subtract(l.toVector()); double dot = to.dot(dir); if (dot > 0 && dot < dist && to.getCrossProduct(dir).length() < 2.0) ((LivingEntity)e).damage(dmg, p); }
        p.setVelocity(dir.multiply(2.0).setY(0.3));
    }

    private void holyBeam(Player p, Map<String,Object> c) {
        double dmg = toD(c.getOrDefault("damage",15)); boolean heal = toB(c.getOrDefault("heal_allies",true)); double ha = toD(c.getOrDefault("heal_amount",6)); double range = toD(c.getOrDefault("beam_range",30));
        Location eye = p.getEyeLocation(); Vector dir = eye.getDirection();
        for (Entity e : p.getWorld().getNearbyEntities(eye, range, range, range)) if (e instanceof LivingEntity && !e.equals(p)) { Vector to = e.getLocation().toVector().subtract(eye.toVector()); double dot = to.dot(dir); if (dot > 0 && dot < range && to.getCrossProduct(dir).length() < 1.5) ((LivingEntity)e).damage(dmg, p); }
        if (heal) for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 10, 10, 10)) if (e instanceof Player && !e.equals(p)) { Player a = (Player)e; a.setHealth(Math.min(a.getHealth()+ha, a.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue())); }
        new BukkitRunnable() { double d = 0; public void run() { if (d >= range) { cancel(); return; } p.getWorld().spawnParticle(Particle.END_ROD, eye.clone().add(dir.clone().multiply(d)), 2, 0, 0, 0, 0.05); d += 0.5; } }.runTaskTimer(plugin, 0L, 1L);
    }

    private void elementalOverload(Player p, Map<String,Object> c) {
        double dmg = toD(c.getOrDefault("damage",10)); int bd = toI(c.getOrDefault("burn_duration",5))*20, rd = toI(c.getOrDefault("regen_duration",5))*20;
        Location l = p.getLocation(); double r = 8;
        for (Entity e : p.getWorld().getNearbyEntities(l, r, r, r)) if (e instanceof LivingEntity && !e.equals(p)) { ((LivingEntity)e).damage(dmg, p); e.setFireTicks(bd); }
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, rd, 1));
    }

    private void playSound(Player p, String s) { if (s == null || s.isEmpty()) return; try { p.playSound(p.getLocation(), Sound.valueOf(s), 1f, 1f); } catch (IllegalArgumentException ignored) {} }
    private int toI(Object o) { return (o instanceof Number) ? ((Number)o).intValue() : 0; }
    private double toD(Object o) { return (o instanceof Number) ? ((Number)o).doubleValue() : 0; }
    private boolean toB(Object o) { return (o instanceof Boolean) && (Boolean) o; }
}
