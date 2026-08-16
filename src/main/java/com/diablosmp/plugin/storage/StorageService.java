package com.diablosmp.plugin.storage;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.PlayerData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StorageService {
    private final DiabloSMP plugin;
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean isSqlite = false;
    private Connection sqliteConnection;
    private File flatfileDir;

    public StorageService(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void init() {
        String type = plugin.getConfig().getString("storage.type", "FLATFILE").toUpperCase();
        if ("SQLITE".equals(type)) {
            isSqlite = true;
            initSqlite();
        } else {
            isSqlite = false;
            flatfileDir = new File(plugin.getDataFolder(), "playerdata");
            if (!flatfileDir.exists()) {
                flatfileDir.mkdirs();
            }
        }
        startAutoSaveTask();
    }

    private void initSqlite() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "diablosmp.db");
            Class.forName("org.sqlite.JDBC");
            sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement stmt = sqliteConnection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_data (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "data TEXT NOT NULL" +
                        ");");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize SQLite storage! Falling back to FLATFILE: " + e.getMessage());
            isSqlite = false;
            flatfileDir = new File(plugin.getDataFolder(), "playerdata");
            if (!flatfileDir.exists()) {
                flatfileDir.mkdirs();
            }
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, this::loadPlayerData);
    }

    public PlayerData getPlayerDataIfLoaded(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public void savePlayerData(UUID uuid, boolean async) {
        PlayerData data = playerDataMap.get(uuid);
        if (data == null) return;

        Runnable saveTask = () -> {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", data.getUuid().toString());
            json.addProperty("firstJoinClaimed", data.isFirstJoinClaimed());
            json.addProperty("hudEnabled", data.isHudEnabled());
            json.addProperty("pluginEnabledForPlayer", data.isPluginEnabledForPlayer());
            json.addProperty("activeStone", data.getActiveStone() != null ? data.getActiveStone().name() : null);

            List<String> ownedList = new ArrayList<>();
            for (DiabloStoneType type : data.getOwnedStones()) {
                ownedList.add(type.name());
            }
            json.add("ownedStones", gson.toJsonTree(ownedList));

            JsonObject cdObject = new JsonObject();
            long now = System.currentTimeMillis();
            for (Map.Entry<DiabloStoneType, Long> entry : data.getCooldowns().entrySet()) {
                if (entry.getValue() > now) {
                    cdObject.addProperty(entry.getKey().name(), entry.getValue());
                }
            }
            json.add("cooldowns", cdObject);

            String serialized = gson.toJson(json);

            if (isSqlite && sqliteConnection != null) {
                try (PreparedStatement pstmt = sqliteConnection.prepareStatement(
                        "INSERT INTO player_data (uuid, data) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET data = EXCLUDED.data")) {
                    pstmt.setString(1, uuid.toString());
                    pstmt.setString(2, serialized);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().severe("Error saving player data to SQLite for " + uuid + ": " + e.getMessage());
                }
            } else {
                File file = new File(flatfileDir, uuid.toString() + ".json");
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(serialized);
                } catch (IOException e) {
                    plugin.getLogger().severe("Error saving player data to FLATFILE for " + uuid + ": " + e.getMessage());
                }
            }
        };

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, saveTask);
        } else {
            saveTask.run();
        }
    }

    public PlayerData loadPlayerData(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        String serialized = null;

        if (isSqlite && sqliteConnection != null) {
            try (PreparedStatement pstmt = sqliteConnection.prepareStatement("SELECT data FROM player_data WHERE uuid = ?")) {
                pstmt.setString(1, uuid.toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        serialized = rs.getString("data");
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading player data from SQLite for " + uuid + ": " + e.getMessage());
            }
        } else {
            File file = new File(flatfileDir, uuid.toString() + ".json");
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    StringBuilder sb = new StringBuilder();
                    int c;
                    while ((c = reader.read()) != -1) {
                        sb.append((char) c);
                    }
                    serialized = sb.toString();
                } catch (IOException e) {
                    plugin.getLogger().severe("Error loading player data from FLATFILE for " + uuid + ": " + e.getMessage());
                }
            }
        }

        if (serialized != null && !serialized.trim().isEmpty()) {
            try {
                JsonObject json = JsonParser.parseString(serialized).getAsJsonObject();
                if (json.has("firstJoinClaimed")) {
                    data.setFirstJoinClaimed(json.get("firstJoinClaimed").getAsBoolean());
                }
                if (json.has("hudEnabled")) {
                    data.setHudEnabled(json.get("hudEnabled").getAsBoolean());
                }
                if (json.has("pluginEnabledForPlayer")) {
                    data.setPluginEnabledForPlayer(json.get("pluginEnabledForPlayer").getAsBoolean());
                }

                if (json.has("ownedStones")) {
                    for (var elem : json.getAsJsonArray("ownedStones")) {
                        DiabloStoneType type = DiabloStoneType.fromString(elem.getAsString());
                        if (type != null) {
                            data.getOwnedStones().add(type);
                        }
                    }
                }

                if (json.has("activeStone") && !json.get("activeStone").isJsonNull()) {
                    DiabloStoneType active = DiabloStoneType.fromString(json.get("activeStone").getAsString());
                    if (active != null) {
                        data.setActiveStone(active);
                    }
                }

                if (json.has("cooldowns")) {
                    JsonObject cdObject = json.getAsJsonObject("cooldowns");
                    long now = System.currentTimeMillis();
                    for (String key : cdObject.keySet()) {
                        DiabloStoneType type = DiabloStoneType.fromString(key);
                        if (type != null) {
                            long exp = cdObject.get(key).getAsLong();
                            if (exp > now) {
                                data.getCooldowns().put(type, exp);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error parsing player data JSON for " + uuid + ": " + e.getMessage());
            }
        }

        return data;
    }

    public void unloadPlayerData(UUID uuid) {
        savePlayerData(uuid, true);
        playerDataMap.remove(uuid);
    }

    public void saveAll(boolean async) {
        for (UUID uuid : playerDataMap.keySet()) {
            savePlayerData(uuid, async);
        }
    }

    private void startAutoSaveTask() {
        long intervalTicks = plugin.getConfig().getLong("storage.save-interval-seconds", 300L) * 20L;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> saveAll(false), intervalTicks, intervalTicks);
    }

    public void close() {
        saveAll(false);
        if (sqliteConnection != null) {
            try {
                sqliteConnection.close();
            } catch (SQLException ignored) {}
        }
    }
}
