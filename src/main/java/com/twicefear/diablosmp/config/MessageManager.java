package com.twicefear.diablosmp.config;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final DiabloSMP plugin;
    private File file;
    private FileConfiguration cfg;

    public MessageManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        this.cfg = YamlConfiguration.loadConfiguration(file);
        try (InputStream in = plugin.getResource("lang.yml")) {
            if (in != null) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(in, StandardCharsets.UTF_8));
                cfg.setDefaults(defaults);
            }
        } catch (IOException ignored) {
        }
    }

    public String get(String path) {
        String raw = cfg.getString(path, path);
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', raw);
    }

    public String prefixed(String path) {
        return get("prefix") + get(path);
    }

    public String prefixed(String path, String... replacers) {
        String msg = get(path);
        for (int i = 0; i + 1 < replacers.length; i += 2) {
            msg = msg.replace("{" + replacers[i] + "}", replacers[i + 1]);
        }
        return get("prefix") + msg;
    }

    public String plain(String path, String... replacers) {
        String msg = get(path);
        for (int i = 0; i + 1 < replacers.length; i += 2) {
            msg = msg.replace("{" + replacers[i] + "}", replacers[i + 1]);
        }
        return msg;
    }
}
