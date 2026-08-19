package com.twicefear.diablosmp.manager;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final DiabloSMP plugin;
    private File file;
    private FileConfiguration cfg;
    private final Map<UUID, StoneType> absorbed = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> rewarded = new ConcurrentHashMap<>();

    public PlayerDataManager(DiabloSMP plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        this.file = new File(plugin.getDataFolder(), "players.yml");
        if (!file.exists()) {
            try { plugin.getDataFolder().mkdirs(); file.createNewFile(); }
            catch (IOException e) { plugin.getLogger().warning("Could not create players.yml: " + e.getMessage()); }
        }
        this.cfg = YamlConfiguration.loadConfiguration(file);
        absorbed.clear();
        rewarded.clear();
        ConfigurationSection sec = cfg.getConfigurationSection("players");
        if (sec != null) {
            for (String uuidStr : sec.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    String stoneId = sec.getString(uuidStr + ".stone");
                    if (stoneId != null && !stoneId.isEmpty() && !stoneId.equalsIgnoreCase("none")) {
                        StoneType.byId(stoneId).ifPresent(s -> absorbed.put(uuid, s));
                    }
                    rewarded.put(uuid, sec.getBoolean(uuidStr + ".rewarded", false));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveAll() {
        if (cfg == null || file == null) return;
        for (UUID uuid : absorbed.keySet()) {
            String path = "players." + uuid;
            StoneType s = absorbed.get(uuid);
            cfg.set(path + ".stone", s == null ? "none" : s.id());
            cfg.set(path + ".rewarded", rewarded.getOrDefault(uuid, false));
        }
        for (UUID uuid : rewarded.keySet()) {
            if (!absorbed.containsKey(uuid)) {
                String path = "players." + uuid;
                cfg.set(path + ".stone", "none");
                cfg.set(path + ".rewarded", rewarded.get(uuid));
            }
        }
        try { cfg.save(file); }
        catch (IOException e) { plugin.getLogger().warning("Could not save players.yml: " + e.getMessage()); }
    }

    public StoneType getAbsorbedStone(UUID uuid) { return absorbed.get(uuid); }
    public void setAbsorbedStone(UUID uuid, StoneType type) {
        if (type == null) absorbed.remove(uuid);
        else absorbed.put(uuid, type);
        saveAll();
    }
    public boolean hasAbsorbed(UUID uuid) { return absorbed.containsKey(uuid); }
    public boolean hasReceivedReward(UUID uuid) { return rewarded.getOrDefault(uuid, false); }
    public void setReceivedReward(UUID uuid, boolean b) { rewarded.put(uuid, b); saveAll(); }
}
