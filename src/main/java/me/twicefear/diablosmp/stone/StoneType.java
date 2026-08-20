package me.twicefear.diablosmp.stone;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum StoneType {
    EARTH_SMASHER(
        "earth_smasher",
        ChatColor.GOLD + "Earth Smasher Stone",
        1001,
        "\uE011",
        "Rock Lift & Launch",
        "Earth Domain & Pillars",
        20,
        45
    ),
    FLAME_LORD(
        "flame_lord",
        ChatColor.RED + "Flame Lord Stone",
        1002,
        "\uE012",
        "Infernal Dragon Strike",
        "Hellfire Supernova",
        18,
        40
    ),
    VOID_WALKER(
        "void_walker",
        ChatColor.DARK_PURPLE + "Void Walker Stone",
        1003,
        "\uE013",
        "Dimensional Rip",
        "Singularity Event",
        15,
        50
    ),
    FROST_MONARCH(
        "frost_monarch",
        ChatColor.AQUA + "Frost Monarch Stone",
        1004,
        "\uE014",
        "Glacial Rampart",
        "Absolute Zero Domain",
        16,
        42
    ),
    LIGHTNING_OVERLORD(
        "lightning_overlord",
        ChatColor.YELLOW + "Lightning Overlord Stone",
        1005,
        "\uE015",
        "Zeus Bolt Strike",
        "Raijin Storm Aura",
        12,
        35
    ),
    SHADOW_REAPER(
        "shadow_reaper",
        ChatColor.DARK_GRAY + "Shadow Reaper Stone",
        1006,
        "\uE016",
        "Soul Scythe Sweep",
        "Death Shadow Realm",
        14,
        48
    ),
    VENOM_HYDRA(
        "venom_hydra",
        ChatColor.DARK_GREEN + "Venom Hydra Stone",
        1007,
        "\uE017",
        "Corrosive Wave",
        "Toxic Miasma Swarm",
        15,
        38
    ),
    CELESTIAL_WARDEN(
        "celestial_warden",
        ChatColor.YELLOW + "Celestial Warden Stone",
        1008,
        "\uE018",
        "Solar Beam Cannon",
        "Sanctuary Shield",
        22,
        60
    ),
    WIND_TEMPEST(
        "wind_tempest",
        ChatColor.WHITE + "Wind Tempest Stone",
        1009,
        "\uE019",
        "Gale Tornado Vortex",
        "Sky Blade Dash",
        10,
        30
    ),
    BLOOD_BERSERKER(
        "blood_berserker",
        ChatColor.DARK_RED + "Blood Berserker Stone",
        1010,
        "\uE01A",
        "Vampiric Burst",
        "Blood Awakening",
        16,
        45
    ),
    GRAVITY_MASTER(
        "gravity_master",
        ChatColor.LIGHT_PURPLE + "Gravity Master Stone",
        1011,
        "\uE01B",
        "Gravitational Crush",
        "Anti-Gravity Burst",
        20,
        50
    ),
    TIME_WEAVER(
        "time_weaver",
        ChatColor.BLUE + "Time Weaver Stone",
        1012,
        "\uE01C",
        "Chrono Rewind Dash",
        "Time Stasis Zone",
        25,
        60
    ),
    PHANTOM_ASSASSIN(
        "phantom_assassin",
        ChatColor.GRAY + "Phantom Assassin Stone",
        1013,
        "\uE01D",
        "Shadow Veil",
        "Phantom Decoy Ambush",
        12,
        35
    ),
    IRON_TITAN(
        "iron_titan",
        ChatColor.DARK_AQUA + "Iron Titan Stone",
        1014,
        "\uE01E",
        "Titan Shield Charge",
        "Fortress Earth Shatter",
        15,
        40
    ),
    CHAOS_ARCHON(
        "chaos_archon",
        ChatColor.MAGIC + "Chaos Archon Stone",
        1015,
        "\uE01F",
        "Metamorphosis Blast",
        "Cataclysmic Meteor",
        30,
        75
    );

    private final String id;
    private final String displayName;
    private final int customModelData;
    private final String hudSymbol;
    private final String primaryAbilityName;
    private final String secondaryAbilityName;
    private final int primaryCooldown; // seconds
    private final int secondaryCooldown; // seconds

    StoneType(String id, String displayName, int customModelData, String hudSymbol,
              String primaryAbilityName, String secondaryAbilityName,
              int primaryCooldown, int secondaryCooldown) {
        this.id = id;
        this.displayName = displayName;
        this.customModelData = customModelData;
        this.hudSymbol = hudSymbol;
        this.primaryAbilityName = primaryAbilityName;
        this.secondaryAbilityName = secondaryAbilityName;
        this.primaryCooldown = primaryCooldown;
        this.secondaryCooldown = secondaryCooldown;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getHudSymbol() {
        return hudSymbol;
    }

    public String getPrimaryAbilityName() {
        return primaryAbilityName;
    }

    public String getSecondaryAbilityName() {
        return secondaryAbilityName;
    }

    public int getPrimaryCooldown() {
        return primaryCooldown;
    }

    public int getSecondaryCooldown() {
        return secondaryCooldown;
    }

    public static StoneType fromId(String id) {
        for (StoneType type : values()) {
            if (type.id.equalsIgnoreCase(id) || type.name().equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
    }
}
