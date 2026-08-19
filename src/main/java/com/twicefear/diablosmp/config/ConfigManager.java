package com.twicefear.diablosmp.config;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final DiabloSMP plugin;
    private FileConfiguration cfg;

    private String smpName;
    private int graceMinutes;
    private int graceSeconds;
    private double borderStart;
    private double borderEnd;
    private boolean borderInfinite;
    private int expandSeconds;

    private int joinDuration;
    private double joinLift;
    private String joinStone;

    private boolean allowDrop;
    private boolean keepOnDeath;
    private boolean allowStore;
    private boolean dropOnPvpDeath;

    private int absorbShifts;
    private int absorbAnimation;

    private int cooldownRefresh;

    private final List<String> announcements = new ArrayList<>();
    private final Map<String, int[]> cooldowns = new HashMap<>();
    private final Map<String, String> cooldownChars = new HashMap<>();
    private boolean debug;

    public ConfigManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();

        this.smpName = cfg.getString("smp-name", "DiabloSmp");
        this.graceMinutes = cfg.getInt("grace.default-minutes", 10);
        this.graceSeconds = cfg.getInt("grace.default-seconds", 0);

        this.borderStart = cfg.getDouble("world-border.start-size", 20);
        String endStr = cfg.getString("world-border.end-size", "20000");
        this.borderInfinite = "infinity".equalsIgnoreCase(endStr);
        this.borderEnd = borderInfinite ? Double.MAX_VALUE : Double.parseDouble(endStr);
        this.expandSeconds = cfg.getInt("world-border.expand-seconds", 60);

        this.joinDuration = cfg.getInt("join-reward.duration-seconds", 8);
        this.joinLift = cfg.getDouble("join-reward.lift-height", 3.0);
        this.joinStone = cfg.getString("join-reward.stone", "RANDOM");

        this.allowDrop = cfg.getBoolean("stones.allow-drop", false);
        this.keepOnDeath = cfg.getBoolean("stones.keep-on-death", true);
        this.allowStore = cfg.getBoolean("stones.allow-store", false);
        this.dropOnPvpDeath = cfg.getBoolean("stones.drop-on-pvp-death", false);

        this.absorbShifts = cfg.getInt("absorb.required-shifts", 3);
        this.absorbAnimation = cfg.getInt("absorb.animation-seconds", 4);

        this.cooldownRefresh = cfg.getInt("cooldown.refresh-ticks", 2);

        this.announcements.clear();
        this.announcements.addAll(cfg.getStringList("announcements"));
        if (announcements.isEmpty()) {
            announcements.add("&c&l10");
            announcements.add("&6&lDiabloSmp");
            announcements.add("&e&lFIGHT BEGINS NOW");
        }

        this.cooldowns.clear();
        var section = cfg.getConfigurationSection("cooldowns");
        if (section != null) {
            for (String stone : section.getKeys(false)) {
                var sub = section.getConfigurationSection(stone);
                if (sub != null) {
                    cooldowns.put(stone.toLowerCase(), new int[]{
                            sub.getInt("primary", 30),
                            sub.getInt("secondary", 90)
                    });
                }
            }
        }

        this.cooldownChars.clear();
        cooldownChars.put("primary-ready", translate(cfg.getString("cooldown.primary-ready-char", "\\uE001")));
        cooldownChars.put("primary-segment", translate(cfg.getString("cooldown.primary-segment-char", "\\uE002")));
        cooldownChars.put("primary-empty", translate(cfg.getString("cooldown.primary-empty-char", "\\uE003")));
        cooldownChars.put("secondary-ready", translate(cfg.getString("cooldown.secondary-ready-char", "\\uE004")));
        cooldownChars.put("secondary-segment", translate(cfg.getString("cooldown.secondary-segment-char", "\\uE005")));
        cooldownChars.put("secondary-empty", translate(cfg.getString("cooldown.secondary-empty-char", "\\uE006")));
        cooldownChars.put("stone-unknown", translate(cfg.getString("cooldown.stone-icon-unknown-char", "\\uE010")));

        this.debug = cfg.getBoolean("debug", false);
    }

    private String translate(String s) {
        if (s == null) return "";
        if (s.startsWith("\\u") && s.length() >= 6) {
            try {
                int cp = Integer.parseInt(s.substring(2, 6), 16);
                return new String(Character.toChars(cp));
            } catch (Exception ignored) {
            }
        }
        return s;
    }

    public String smpName() { return smpName; }
    public void setSmpName(String n) { this.smpName = n; }
    public int graceMinutes() { return graceMinutes; }
    public int graceSeconds() { return graceSeconds; }
    public double borderStart() { return borderStart; }
    public double borderEnd() { return borderEnd; }
    public boolean borderInfinite() { return borderInfinite; }
    public int expandSeconds() { return expandSeconds; }
    public int joinDuration() { return joinDuration; }
    public double joinLift() { return joinLift; }
    public String joinStone() { return joinStone; }
    public boolean allowDrop() { return allowDrop; }
    public boolean keepOnDeath() { return keepOnDeath; }
    public boolean allowStore() { return allowStore; }
    public boolean dropOnPvpDeath() { return dropOnPvpDeath; }
    public int absorbShifts() { return absorbShifts; }
    public int absorbAnimation() { return absorbAnimation; }
    public int cooldownRefreshTicks() { return cooldownRefresh; }
    public List<String> announcements() { return announcements; }
    public int[] cooldownFor(String stone) { return cooldowns.getOrDefault(stone.toLowerCase(), new int[]{30, 90}); }
    public String cooldownChar(String key) { return cooldownChars.getOrDefault(key, ""); }
    public boolean isDebugEnabled() { return debug; }
}
