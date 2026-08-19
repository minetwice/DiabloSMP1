package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.abilities.*;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.entity.Player;

public class AbilityManager {

    private final DiabloSMP plugin;
    private final InfernalAbility infernalAbility;
    private final AbyssalAbility abyssalAbility;
    private final EarthquakeAbility earthquakeAbility;
    private final TempestAbility tempestAbility;
    private final ShadowAbility shadowAbility;
    private final RadiantAbility radiantAbility;
    private final FrostbiteAbility frostbiteAbility;
    private final ThunderboltAbility thunderboltAbility;
    private final BloodmoonAbility bloodmoonAbility;
    private final NatureAbility natureAbility;
    private final PhantomAbility phantomAbility;
    private final ChaosAbility chaosAbility;
    private final DragonheartAbility dragonheartAbility;
    private final VoidwalkerAbility voidwalkerAbility;
    private final CelestialAbility celestialAbility;

    public AbilityManager(DiabloSMP plugin) {
        this.plugin = plugin;
        this.infernalAbility = new InfernalAbility(plugin);
        this.abyssalAbility = new AbyssalAbility(plugin);
        this.earthquakeAbility = new EarthquakeAbility(plugin);
        this.tempestAbility = new TempestAbility(plugin);
        this.shadowAbility = new ShadowAbility(plugin);
        this.radiantAbility = new RadiantAbility(plugin);
        this.frostbiteAbility = new FrostbiteAbility(plugin);
        this.thunderboltAbility = new ThunderboltAbility(plugin);
        this.bloodmoonAbility = new BloodmoonAbility(plugin);
        this.natureAbility = new NatureAbility(plugin);
        this.phantomAbility = new PhantomAbility(plugin);
        this.chaosAbility = new ChaosAbility(plugin);
        this.dragonheartAbility = new DragonheartAbility(plugin);
        this.voidwalkerAbility = new VoidwalkerAbility(plugin);
        this.celestialAbility = new CelestialAbility(plugin);
    }

    public void execute(Player player, StoneType type, boolean primary) {
        switch (type) {
            case INFERNAL_CORE -> {
                if (primary) infernalAbility.primary(player);
                else infernalAbility.secondary(player);
            }
            case ABYSSAL_SHARD -> {
                if (primary) abyssalAbility.primary(player);
                else abyssalAbility.secondary(player);
            }
            case EARTHQUAKE_RELIC -> {
                if (primary) earthquakeAbility.primary(player);
                else earthquakeAbility.secondary(player);
            }
            case TEMPEST_ORB -> {
                if (primary) tempestAbility.primary(player);
                else tempestAbility.secondary(player);
            }
            case SHADOW_FANG -> {
                if (primary) shadowAbility.primary(player);
                else shadowAbility.secondary(player);
            }
            case RADIANT_PRISM -> {
                if (primary) radiantAbility.primary(player);
                else radiantAbility.secondary(player);
            }
            case FROSTBITE_CRYSTAL -> {
                if (primary) frostbiteAbility.primary(player);
                else frostbiteAbility.secondary(player);
            }
            case THUNDERBOLT_CORE -> {
                if (primary) thunderboltAbility.primary(player);
                else thunderboltAbility.secondary(player);
            }
            case BLOODMOON_GEM -> {
                if (primary) bloodmoonAbility.primary(player);
                else bloodmoonAbility.secondary(player);
            }
            case NATURES_WRATH -> {
                if (primary) natureAbility.primary(player);
                else natureAbility.secondary(player);
            }
            case PHANTOM_ECHO -> {
                if (primary) phantomAbility.primary(player);
                else phantomAbility.secondary(player);
            }
            case CHAOS_FRAGMENT -> {
                if (primary) chaosAbility.primary(player);
                else chaosAbility.secondary(player);
            }
            case DRAGONHEART_SCALE -> {
                if (primary) dragonheartAbility.primary(player);
                else dragonheartAbility.secondary(player);
            }
            case VOIDWALKER_STONE -> {
                if (primary) voidwalkerAbility.primary(player);
                else voidwalkerAbility.secondary(player);
            }
            case CELESTIAL_STAR -> {
                if (primary) celestialAbility.primary(player);
                else celestialAbility.secondary(player);
            }
        }
    }
}
