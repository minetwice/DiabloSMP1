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
            if (userData.isCooldowned(primaryKey)) {
                double rem = userData.getRemainingCooldownSeconds(primaryKey);
                leftBar = ChatColor.RED + "R: " + String.format("%.1fs", rem);
            } else {
                leftBar = ChatColor.GREEN + "\uE001 &aAbility Ready!";
            }

            // Stone Icon
            stoneIcon = " " + ChatColor.GOLD + stone.getHudSymbol() + " ";

            // Shift + Right Click Cooldown
            String secondaryKey = stone.getId() + "_secondary";
            if (userData.isCooldowned(secondaryKey)) {
                double rem = userData.getRemainingCooldownSeconds(secondaryKey);
                rightBar = ChatColor.RED + "Shift+R: " + String.format("%.1fs", rem);
            } else {
                rightBar = ChatColor.GREEN + "\uE001 &aAbility Ready!";
            }
        }

        return ChatColor.translateAlternateColorCodes('&', leftBar + stoneIcon + rightBar);
    }
}
