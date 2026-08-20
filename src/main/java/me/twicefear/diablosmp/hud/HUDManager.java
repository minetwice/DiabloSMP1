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

    // Custom glyph mappings in font/default.json
    private static final String UNABSORBED_GLYPH = "\uE000"; // Framed Question Mark icon
    private static final String READY_GLYPH = "\uE001";      // Custom Ready icon

    // Custom Bar glyphs \uE002 (0%) to \uE00C (100%)
    private static final char BAR_BASE_GLYPH = '\uE002';

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
            leftBar = ChatColor.GRAY + "R: " + getBarGlyph(0) + " Unabsorbed";
            stoneIcon = " " + UNABSORBED_GLYPH + " "; // Centered framed question mark
            rightBar = ChatColor.GRAY + "Shift+R: " + getBarGlyph(0) + " Unabsorbed";
        } else {
            // Right Click Cooldown
            String primaryKey = stone.getId() + "_primary";
            double primaryRem = userData.getRemainingCooldownSeconds(primaryKey);
            leftBar = ChatColor.GOLD + "R: " + formatCustomBar(primaryRem, stone.getPrimaryCooldown());

            // Stone Icon in center
            stoneIcon = " " + ChatColor.WHITE + stone.getHudSymbol() + " ";

            // Shift + Right Click Cooldown
            String secondaryKey = stone.getId() + "_secondary";
            double secondaryRem = userData.getRemainingCooldownSeconds(secondaryKey);
            rightBar = ChatColor.GOLD + "Shift+R: " + formatCustomBar(secondaryRem, stone.getSecondaryCooldown());
        }

        return ChatColor.translateAlternateColorCodes('&', leftBar + stoneIcon + rightBar);
    }

    private String formatCustomBar(double currentSeconds, double totalSeconds) {
        if (currentSeconds <= 0) {
            return READY_GLYPH + " " + ChatColor.GREEN + "Ready!";
        }

        double progress = Math.max(0.0, Math.min(1.0, 1.0 - (currentSeconds / totalSeconds)));
        int index = (int) Math.round(progress * 10.0); // 0 to 10
        String barGlyph = getBarGlyph(index);

        return barGlyph + " " + ChatColor.RED + String.format("%.1fs", currentSeconds);
    }

    private String getBarGlyph(int index) {
        index = Math.max(0, Math.min(10, index));
        char c = (char) (BAR_BASE_GLYPH + index);
        return String.valueOf(c);
    }
}
