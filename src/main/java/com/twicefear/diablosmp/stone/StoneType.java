package com.twicefear.diablosmp.stone;

import java.util.Arrays;
import java.util.Optional;

/**
 * The 15 Diablo stones. Each has two abilities (primary = right click,
 * secondary = shift + right click).
 */
public enum StoneType {

    EARTHQUAKE("earthquake", "&6Earthquake", "&7Tectonic Might"),
    INFERNO("inferno", "&cInferno", "&7Flame of Destruction"),
    TEMPEST("tempest", "&bTempest", "&7Wrath of Skies"),
    FROSTBITE("frostbite", "&3Frostbite", "&7Eternal Winter"),
    SHADOW("shadow", "&8Shadow", "&7Veil of Darkness"),
    HOLY("holy", "&fHoly", "&7Radiant Judgement"),
    VOID("void", "&5Void", "&7Tear in Reality"),
    NATURE("nature", "&aNature", "&7Wild Genesis"),
    LIGHTNING("lightning", "&eLightning", "&7Storm Caller"),
    BLOOD("blood", "&4Blood", "&7Crimson Pact"),
    GRAVITY("gravity", "&9Gravity", "&7Singularity"),
    SOUL("soul", "&dSoul", "&7Echo of Spirits"),
    ARCANE("arcane", "&dArcane", "&7Mystic Surge"),
    PLAGUE("plague", "&2Plague", "&7Withering Blight"),
    CHRONOS("chronos", "&6Chronos", "&7Time Fracture");

    private final String id;
    private final String display;
    private final String lore;

    StoneType(String id, String display, String lore) {
        this.id = id;
        this.display = display;
        this.lore = lore;
    }

    public String id() {
        return id;
    }

    public String display() {
        return display;
    }

    public String lore() {
        return lore;
    }

    public static Optional<StoneType> byId(String id) {
        return Arrays.stream(values())
                .filter(s -> s.id.equalsIgnoreCase(id))
                .findFirst();
    }

    public static StoneType random() {
        StoneType[] all = values();
        return all[(int) (Math.random() * all.length)];
    }
}
