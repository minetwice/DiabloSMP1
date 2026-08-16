package com.diablosmp.plugin.service;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CastingService {
    private final DiabloSMP plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final NamespacedKey stoneKey;
    private final Set<Player> activeCasters = ConcurrentHashMap.newKeySet();

    public CastingService(DiabloSMP plugin) {
        this.plugin = plugin;
        this.stoneKey = new NamespacedKey(plugin, "diablo_stone");
    }

    public boolean tryCast(Player player) {
        if (!player.hasPermission("diablosmp.cast")) {
            sendMsg(player, "no-permission");
            return false;
        }

        if (activeCasters.contains(player)) {
            sendMsg(player, "cast-already-casting");
            return false;
        }

        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
        if (!data.isPluginEnabledForPlayer()) return false;

        boolean itemMode = plugin.getConfig().getBoolean("stones.item-mode", false);
        DiabloStoneType targetStone = null;

        if (itemMode) {
            ItemStack held = player.getInventory().getItemInMainHand();
            targetStone = getStoneFromItem(held);
            if (targetStone == null) return false;
        } else {
            targetStone = data.getActiveStone();
        }

        if (targetStone == null) {
            sendMsg(player, "no-stone-owned");
            return false;
        }

        if (!data.hasStone(targetStone) && !itemMode) {
            sendMsg(player, "stone-not-owned", "{stone}", targetStone.name());
            return false;
        }

        StoneConfig config = plugin.getConfigManager().getStoneConfig(targetStone);
        if (config == null || !config.isEnabled()) return false;

        if (plugin.getCooldownService().isOnCooldown(player, targetStone)) {
            double remainingSec = plugin.getCooldownService().getRemainingSeconds(player, targetStone);
            String cdMsg = plugin.getConfigManager().getMessage("cooldown-active")
                    .replace("{stone}", config.getDisplayName())
                    .replace("{seconds}", String.format("%.1f", remainingSec));
            player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() + cdMsg));
            return false;
        }

        // Start cast
        activeCasters.add(player);
        plugin.getCooldownService().startCooldown(player, targetStone);

        // Movie-style Emote Title / Subtitle on cast
        Component titleComp = miniMessage.deserialize("<gradient:#FF5555:#AA0000>❖ " + targetStone.name() + " ❖</gradient>");
        Component subTitleComp = miniMessage.deserialize("<gray>Unleashing " + config.getDisplayName() + "</gray>");
        Title.Times times = Title.Times.times(Duration.ofMillis(200), Duration.ofMillis(1200), Duration.ofMillis(300));
        player.showTitle(Title.title(titleComp, subTitleComp, times));

        boolean success = plugin.getAbilityManager().castAbility(player, targetStone);
        if (success) {
            data.setLastCastTimestamp(System.currentTimeMillis());
            plugin.getHudService().updateHud(player);
        }

        activeCasters.remove(player);
        return success;
    }

    public ItemStack createStoneItem(DiabloStoneType type) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(type);
        ItemStack item = new ItemStack(config != null ? config.getMaterial() : type.getFallbackMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(miniMessage.deserialize(config != null ? config.getDisplayName() : type.getDefaultDisplayName()));

            List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
            if (config != null && config.getDescription() != null) {
                for (String line : config.getDescription()) {
                    lore.add(miniMessage.deserialize(line));
                }
            }
            lore.add(miniMessage.deserialize("<dark_purple><italic>Soulbound Diablo Stone</italic></dark_purple>"));
            meta.lore(lore);

            if (config != null) {
                meta.setCustomModelData(config.getCustomModelData());
            }

            meta.getPersistentDataContainer().set(stoneKey, PersistentDataType.STRING, type.name());
            item.setItemMeta(meta);
        }
        return item;
    }

    public DiabloStoneType getStoneFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        String val = meta.getPersistentDataContainer().get(stoneKey, PersistentDataType.STRING);
        return DiabloStoneType.fromString(val);
    }

    public boolean isSoulbound(ItemStack item) {
        return getStoneFromItem(item) != null;
    }

    private void sendMsg(Player player, String key, String... replacements) {
        String msg = plugin.getConfigManager().getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                msg = msg.replace(replacements[i], replacements[i + 1]);
            }
        }
        player.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() + msg));
    }
}
