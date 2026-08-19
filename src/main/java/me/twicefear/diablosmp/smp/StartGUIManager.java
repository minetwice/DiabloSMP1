package me.twicefear.diablosmp.smp;

import me.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StartGUIManager implements Listener {

    private final DiabloSMP plugin;
    private final SMPManager smpManager;
    private static final String GUI_TITLE = ChatColor.DARK_RED + "" + ChatColor.BOLD + "DiabloSMP Start Config";

    private final Map<UUID, Integer> selectedMinutes = new HashMap<>();
    private final Map<UUID, Integer> selectedSeconds = new HashMap<>();

    public StartGUIManager(DiabloSMP plugin, SMPManager smpManager) {
        this.plugin = plugin;
        this.smpManager = smpManager;
    }

    public void openGUI(Player player) {
        selectedMinutes.putIfAbsent(player.getUniqueId(), 5);
        selectedSeconds.putIfAbsent(player.getUniqueId(), 0);

        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        int mins = selectedMinutes.get(player.getUniqueId());
        int secs = selectedSeconds.get(player.getUniqueId());

        // Minutes control (-5m, -1m, Display, +1m, +5m)
        gui.setItem(10, createGuiItem(Material.RED_WOOL, ChatColor.RED + "-5 Minutes"));
        gui.setItem(11, createGuiItem(Material.RED_CONCRETE, ChatColor.RED + "-1 Minute"));
        gui.setItem(12, createGuiItem(Material.CLOCK, ChatColor.GOLD + "Minutes: " + ChatColor.YELLOW + mins,
                ChatColor.GRAY + "Current Grace Minutes"));
        gui.setItem(13, createGuiItem(Material.GREEN_CONCRETE, ChatColor.GREEN + "+1 Minute"));
        gui.setItem(14, createGuiItem(Material.GREEN_WOOL, ChatColor.GREEN + "+5 Minutes"));

        // Seconds control (-10s, Display, +10s)
        gui.setItem(19, createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-10 Seconds"));
        gui.setItem(21, createGuiItem(Material.COMPASS, ChatColor.GOLD + "Seconds: " + ChatColor.YELLOW + secs,
                ChatColor.GRAY + "Current Grace Seconds"));
        gui.setItem(22, createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "+10 Seconds"));

        // Confirm button
        gui.setItem(16, createGuiItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "" + ChatColor.BOLD + "START DIABLO SMP",
                ChatColor.GRAY + "Click to launch SMP with grace period: " + String.format("%02d:%02d", mins, secs)));

        player.openInventory(gui);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();

        int mins = selectedMinutes.getOrDefault(player.getUniqueId(), 5);
        int secs = selectedSeconds.getOrDefault(player.getUniqueId(), 0);

        boolean updated = false;

        switch (slot) {
            case 10:
                mins = Math.max(0, mins - 5);
                updated = true;
                break;
            case 11:
                mins = Math.max(0, mins - 1);
                updated = true;
                break;
            case 13:
                mins += 1;
                updated = true;
                break;
            case 14:
                mins += 5;
                updated = true;
                break;
            case 19:
                secs = Math.max(0, secs - 10);
                updated = true;
                break;
            case 22:
                secs = (secs + 10) % 60;
                updated = true;
                break;
            case 16:
                player.closeInventory();
                smpManager.startSmp(mins, secs);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                return;
        }

        if (updated) {
            selectedMinutes.put(player.getUniqueId(), mins);
            selectedSeconds.put(player.getUniqueId(), secs);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
            openGUI(player);
        }
    }
}
