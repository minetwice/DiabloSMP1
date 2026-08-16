package com.diablosmp.plugin.ability;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.ability.impl.*;
import com.diablosmp.plugin.model.DiabloStoneType;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;

public class AbilityManager {
    private final DiabloSMP plugin;
    private final Map<DiabloStoneType, DiabloAbility> abilities = new EnumMap<>(DiabloStoneType.class);

    public AbilityManager(DiabloSMP plugin) {
        this.plugin = plugin;
        registerAbilities();
    }

    private void registerAbilities() {
        abilities.put(DiabloStoneType.SHARD, new ShardAbility(plugin));
        abilities.put(DiabloStoneType.EMBER, new EmberAbility(plugin));
        abilities.put(DiabloStoneType.HALO, new HaloAbility(plugin));
        abilities.put(DiabloStoneType.ROOT, new RootAbility(plugin));
        abilities.put(DiabloStoneType.VOID, new VoidAbility(plugin));
        abilities.put(DiabloStoneType.FROST, new FrostAbility(plugin));
        abilities.put(DiabloStoneType.STORM, new StormAbility(plugin));
        abilities.put(DiabloStoneType.BLOOD, new BloodAbility(plugin));
        abilities.put(DiabloStoneType.SERAPH, new SeraphAbility(plugin));
        abilities.put(DiabloStoneType.GRAVE, new GraveAbility(plugin));
        abilities.put(DiabloStoneType.MIRAGE, new MirageAbility(plugin));
        abilities.put(DiabloStoneType.LANCE, new LanceAbility(plugin));
        abilities.put(DiabloStoneType.ABYSS, new AbyssAbility(plugin));
        abilities.put(DiabloStoneType.CHRONO, new ChronoAbility(plugin));
        abilities.put(DiabloStoneType.OMEGA, new OmegaAbility(plugin));
    }

    public DiabloAbility getAbility(DiabloStoneType type) {
        return abilities.get(type);
    }

    public boolean castAbility(Player player, DiabloStoneType type) {
        DiabloAbility ability = getAbility(type);
        if (ability != null) {
            return ability.cast(player);
        }
        return false;
    }

    public void cleanupAll() {
        for (DiabloAbility ability : abilities.values()) {
            if (ability != null) {
                ability.cleanup();
            }
        }
    }
}
