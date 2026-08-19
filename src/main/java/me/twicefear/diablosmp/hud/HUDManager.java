package me.twicefear.diablosmp.hud;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HUDManager {

    private final DiabloSMP plugin;

    public HUDManager(DiabloSMP plugin) {
        this.plugin = plugin;
        startHudTask();
    }

    private void startHudTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfig().getBoolean("cooldowns.display_in_actionbar", true)) {
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.isOnline()) continue;
                    UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());

                    String actionbarContent = buildActionBarContent(player, userData);
                    if (actionbarContent != null && !actionbarContent.isEmpty()) {
                        player.sendActionBar(actionbarContent);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L); // Update every 5 ticks (0.25s) for smooth display
    }

    private String buildActionBarContent(Player player, UserData userData) {
        StoneType stone = userData.getAbsorbedStone();

        String leftBar;
        String stoneIcon;
        String rightBar;

        if (stone == null) {
            leftBar = ChatColor.GRAY + "[R: No Stone]";
            stoneIcon = ChatColor.GRAY + " \uE000? "; // Gray question mark
            rightBar = ChatColor.GRAY + "[Shift+R: No Stone]";
        } else {
            // Right Click Cooldown
            String primaryKey = stone.getId() + "_primary";
            double primaryRem = userData.getRemainingCooldownSeconds(primaryKey);
            leftBar = "R: " + formatProgressBar(primaryRem, stone.getPrimaryCooldown());

            // Stone Icon
            stoneIcon = " " + ChatColor.GOLD + stone.getHudSymbol() + " ";

            // Shift + Right Click Cooldown
            String secondaryKey = stone.getId() + "_secondary";
            double secondaryRem = userData.getRemainingCooldownSeconds(secondaryKey);
            rightBar = "Shift+R: " + formatProgressBar(secondaryRem, stone.getSecondaryCooldown());
        }

        return ChatColor.translateAlternateColorCodes('&', leftBar + stoneIcon + rightBar);
    }

    private String formatProgressBar(double currentSeconds, double totalSeconds) {
        if (currentSeconds <= 0) {
            return ChatColor.GREEN + "\uE001 Ability Ready!";
        }
        int totalBars = 6;
        double progress = Math.max(0.0, Math.min(1.0, 1.0 - (currentSeconds / totalSeconds)));
        int filledBars = (int) Math.round(progress * totalBars);

        StringBuilder bar = new StringBuilder(ChatColor.DARK_GRAY + "[");
        for (int i = 0; i < totalBars; i++) {
            if (i < filledBars) {
                bar.append(ChatColor.GREEN).append("|");
            } else {
                bar.append(ChatColor.RED).append("|");
            }
        }
        bar.append(ChatColor.DARK_GRAY).append("] ").append(ChatColor.RED).append(String.format("%.1fs", currentSeconds));
        return bar.toString();
    }
}
