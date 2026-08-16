package com.diablosmp.plugin.ability;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.ability.impl.*;
import com.diablosmp.plugin.model.DiabloStoneType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityManager {
    private final DiabloSMP plugin;
    private final Map<DiabloStoneType, DiabloAbility> abilities = new EnumMap<>(DiabloStoneType.class);
    private final Set<AbilityCast> activeCasts = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public AbilityManager(DiabloSMP plugin) {
        this.plugin = plugin;
        registerAbilities();
    }

    private void registerAbilities() {
        abilities.put(DiabloStoneType.FROST, new FrostAbility(plugin));
        abilities.put(DiabloStoneType.MIRAGE, new MirageAbility(plugin));
        abilities.put(DiabloStoneType.QUAKE, new QuakeAbility(plugin));
        abilities.put(DiabloStoneType.HELLGATE, new HellgateAbility(plugin));
        abilities.put(DiabloStoneType.VERTIGO, new VertigoAbility(plugin));
        abilities.put(DiabloStoneType.MARIONETTE, new MarionetteAbility(plugin));
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

    public void registerActiveCast(AbilityCast cast) {
        activeCasts.add(cast);
    }

    public void unregisterActiveCast(AbilityCast cast) {
        activeCasts.remove(cast);
    }

    public void cleanupAll() {
        for (AbilityCast cast : activeCasts.toArray(new AbilityCast[0])) {
            if (cast != null) {
                cast.cleanup();
            }
        }
        activeCasts.clear();
    }
}
