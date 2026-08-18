package com.twicefear.diablosmp.ability;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.abilities.*;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {

    private final DiabloSMP plugin;
    private final Map<String, Ability> abilities = new HashMap<>();
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private final Map<Player, String> activeAbilities = new HashMap<>();

    public AbilityRegistry(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        register(new InfernoAscension(plugin));
        register(new FrostfallDominion(plugin));
        register(new TempestStrike(plugin));
        register(new TitansVigor(plugin));
        register(new CycloneAura(plugin));
        register(new TidalSurge(plugin));
        register(new DivineAegis(plugin));
        register(new UmbralEclipse(plugin));
        register(new ArcaneOverflow(plugin));
        register(new VerdantRebirth(plugin));
        register(new BloodCovenant(plugin));
        register(new VoidCollapse(plugin));
        register(new StormcallersFury(plugin));
        register(new EtherealPhase(plugin));
        register(new GravityWell(plugin));
    }

    private void register(Ability ability) {
        abilities.put(ability.getId().toLowerCase(), ability);
    }

    public Ability getAbility(String id) {
        return abilities.get(id.toLowerCase());
    }

    public Collection<Ability> getAllAbilities() {
        return abilities.values();
    }

    public int getAbilityCount() {
        return abilities.size();
    }

    public boolean isOnCooldown(Player player) {
        Long expiry = cooldowns.get(player);
        if (expiry == null) return false;
        if (System.currentTimeMillis() >= expiry) {
            cooldowns.remove(player);
            return false;
        }
        return true;
    }

    public int getRemainingCooldown(Player player) {
        Long expiry = cooldowns.get(player);
        if (expiry == null) return 0;
        int remaining = (int) Math.ceil((expiry - System.currentTimeMillis()) / 1000.0);
        return Math.max(0, remaining);
    }

    public void setCooldown(Player player, int seconds) {
        cooldowns.put(player, System.currentTimeMillis() + (seconds * 1000L));
    }

    public boolean hasActiveAbility(Player player) {
        return activeAbilities.containsKey(player);
    }

    public void setActive(Player player, String abilityId) {
        activeAbilities.put(player, abilityId);
    }

    public void clearActive(Player player) {
        activeAbilities.remove(player);
    }

    public String getActive(Player player) {
        return activeAbilities.get(player);
    }
}
