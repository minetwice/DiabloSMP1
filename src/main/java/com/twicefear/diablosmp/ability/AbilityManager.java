package com.twicefear.diablosmp.ability;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.impl.*;
import com.twicefear.diablosmp.stone.StoneType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Holds the single Ability instance per stone type.
 */
public class AbilityManager {

    private final DiabloSMP plugin;
    private final Map<StoneType, Ability> abilities = new EnumMap<>(StoneType.class);

    public AbilityManager(DiabloSMP plugin) {
        this.plugin = plugin;
        registerAll();
    }

    private void registerAll() {
        abilities.put(StoneType.EARTHQUAKE, new EarthquakeAbility(plugin));
        abilities.put(StoneType.INFERNO, new InfernoAbility(plugin));
        abilities.put(StoneType.TEMPEST, new TempestAbility(plugin));
        abilities.put(StoneType.FROSTBITE, new FrostbiteAbility(plugin));
        abilities.put(StoneType.SHADOW, new ShadowAbility(plugin));
        abilities.put(StoneType.HOLY, new HolyAbility(plugin));
        abilities.put(StoneType.VOID, new VoidAbility(plugin));
        abilities.put(StoneType.NATURE, new NatureAbility(plugin));
        abilities.put(StoneType.LIGHTNING, new LightningAbility(plugin));
        abilities.put(StoneType.BLOOD, new BloodAbility(plugin));
        abilities.put(StoneType.GRAVITY, new GravityAbility(plugin));
        abilities.put(StoneType.SOUL, new SoulAbility(plugin));
        abilities.put(StoneType.ARCANE, new ArcaneAbility(plugin));
        abilities.put(StoneType.PLAGUE, new PlagueAbility(plugin));
        abilities.put(StoneType.CHRONOS, new ChronosAbility(plugin));
    }

    public Ability get(StoneType type) {
        return abilities.get(type);
    }
}
