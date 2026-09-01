package com.sdmine.elementalcore.util;

import com.sdmine.elementalcore.core.CoreType;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.List;

public final class MessageUtil {
    private MessageUtil() {}

    public static String color(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> color(List<String> messages) {
        List<String> result = new ArrayList<>();
        if (messages == null) return result;
        for (String line : messages) result.add(color(line));
        return result;
    }

    public static String strip(String message) {
        if (message == null) return "";
        return ChatColor.stripColor(message);
    }

    public static String formatSockets(CoreType[] sockets) {
        StringBuilder sb = new StringBuilder("&7Sockets: ");
        for (int i = 0; i < sockets.length; i++) {
            if (sockets[i] == null) sb.append("&7[Empty]");
            else sb.append(sockets[i].getChatColor()).append("[").append(sockets[i].getSymbol()).append("]");
            if (i < sockets.length - 1) sb.append(" ");
        }
        return sb.toString();
    }
}
