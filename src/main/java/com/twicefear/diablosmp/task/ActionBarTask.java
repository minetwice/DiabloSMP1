package com.twicefear.diablosmp.task;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTask extends BukkitRunnable {

    private final DiabloSMP plugin;
    private static final int BAR_SEGMENTS = 10;

    public ActionBarTask(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!plugin.smp().isRunning() && !plugin.smp().isGrace()) continue;
            StoneType stone = plugin.players().getAbsorbedStone(player.getUniqueId());
            String primaryBar = buildBar(player, stone, true);
            String secondaryBar = buildBar(player, stone, false);
            String stoneIcon = buildStoneIcon(stone);
            String bar = primaryBar + stoneIcon + secondaryBar;
            Component component = LegacyComponentSerializer.legacySection().deserialize(bar);
            player.sendActionBar(component);
        }
    }

    private String buildBar(Player player, StoneType stone, boolean primary) {
        if (stone == null) {
            String empty = plugin.config().cooldownChar(primary ? "primary-empty" : "secondary-empty");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < BAR_SEGMENTS; i++) sb.append(empty);
            return sb.toString();
        }
        boolean ready = primary
                ? plugin.cooldowns().isPrimaryReady(player.getUniqueId())
                : plugin.cooldowns().isSecondaryReady(player.getUniqueId());
        if (ready) {
            String readyChar = plugin.config().cooldownChar(primary ? "primary-ready" : "secondary-ready");
            return readyChar + " " + net.md_5.bungee.api.ChatColor.GREEN + "Ability Ready!";
        }
        double fraction = primary
                ? plugin.cooldowns().primaryFraction(player.getUniqueId(), stone)
                : plugin.cooldowns().secondaryFraction(player.getUniqueId(), stone);
        int filled = (int) Math.ceil((1.0 - fraction) * BAR_SEGMENTS);
        String segChar = plugin.config().cooldownChar(primary ? "primary-segment" : "secondary-segment");
        String emptyChar = plugin.config().cooldownChar(primary ? "primary-empty" : "secondary-empty");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BAR_SEGMENTS; i++) {
            if (i < filled) sb.append(segChar);
            else sb.append(emptyChar);
        }
        long rem = primary
                ? plugin.cooldowns().primaryRemaining(player.getUniqueId())
                : plugin.cooldowns().secondaryRemaining(player.getUniqueId());
        sb.append(" ").append(net.md_5.bungee.api.ChatColor.RED).append(rem).append("s");
        return sb.toString();
    }

    private String buildStoneIcon(StoneType stone) {
        if (stone == null) {
            String unknown = plugin.config().cooldownChar("stone-unknown");
            return net.md_5.bungee.api.ChatColor.GRAY + unknown;
        }
        int code = 0xE020 + stone.ordinal();
        String icon = new String(Character.toChars(code));
        return net.md_5.bungee.api.ChatColor.WHITE + icon;
    }
}
