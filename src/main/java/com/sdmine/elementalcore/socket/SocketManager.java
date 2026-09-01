package com.sdmine.elementalcore.socket;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SocketManager {
    private final ElementalCorePlugin plugin;
    private final NamespacedKey[] socketKeys;

    public SocketManager(ElementalCorePlugin plugin) {
        this.plugin = plugin;
        this.socketKeys = new NamespacedKey[3];
        for (int i = 0; i < 3; i++)
            socketKeys[i] = new NamespacedKey(plugin, "socket_" + i);
    }

    public CoreType[] readSockets(ItemMeta meta) {
        CoreType[] sockets = new CoreType[3];
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (int i = 0; i < 3; i++) {
            Integer ordinal = pdc.get(socketKeys[i], PersistentDataType.INTEGER);
            if (ordinal != null && ordinal >= 0) {
                CoreType[] values = CoreType.values();
                if (ordinal < values.length) sockets[i] = values[ordinal];
            }
        }
        return sockets;
    }

    public void writeSockets(ItemMeta meta, CoreType[] sockets) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (int i = 0; i < 3; i++) {
            int value = (sockets != null && i < sockets.length && sockets[i] != null) ? sockets[i].ordinal() : -1;
            pdc.set(socketKeys[i], PersistentDataType.INTEGER, value);
        }
    }

    public CoreType[] readSockets(org.bukkit.inventory.ItemStack item) {
        if (item == null || !item.hasItemMeta()) return new CoreType[3];
        return readSockets(item.getItemMeta());
    }

    public void setSocket(ItemMeta meta, int slot, CoreType type) {
        if (slot < 0 || slot >= 3) return;
        meta.getPersistentDataContainer().set(socketKeys[slot], PersistentDataType.INTEGER, type != null ? type.ordinal() : -1);
    }

    public static String generateCombinationKey(CoreType[] sockets) {
        int[] counts = new int[CoreType.values().length];
        for (CoreType t : sockets) if (t != null) counts[t.ordinal()]++;
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (CoreType type : CoreType.values()) {
            int count = counts[type.ordinal()];
            if (count > 0) {
                if (!first) sb.append(",");
                sb.append(type.name().toLowerCase()).append(":").append(count);
                first = false;
            }
        }
        return sb.toString();
    }

    public static int countFilled(CoreType[] sockets) {
        int count = 0;
        for (CoreType t : sockets) if (t != null) count++;
        return count;
    }
}
