package me.twicefear.diablosmp.listener;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbsorptionListener implements Listener {

    private final DiabloSMP plugin;
    private final String GUI_TITLE = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Place Stone to Absorb";
    private final Map<UUID, Inventory> openAbsorbGuis = new HashMap<>();

    public AbsorptionListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        if (!plugin.getSmpManager().isStarted()) return;

        Player player = event.getPlayer();
        UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());

        if (userData.isFirstJoinAnimationActive()) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!plugin.getStoneItemManager().isDiabloStone(mainHand)) {
            userData.resetShiftCount();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - userData.getLastShiftTime() > 1500) {
            userData.resetShiftCount();
        }

        userData.setLastShiftTime(now);
        userData.incrementShiftCount();

        int count = userData.getShiftCount();
        player.sendActionBar(ChatColor.GOLD + "Absorb Charge: " + ChatColor.RED + count + "/3");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.0f + (count * 0.3f));

        if (count >= 3) {
            userData.resetShiftCount();
            openAbsorbGUI(player);
        }
    }

    private void openAbsorbGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }

        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                gui.setItem(i, filler);
            }
        }

        openAbsorbGuis.put(player.getUniqueId(), gui);
        player.openInventory(gui);
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        if (title.equals(GUI_TITLE)) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }

            int slot = event.getRawSlot();
            if (slot >= 0 && slot < 9) {
                if (slot != 4) {
                    event.setCancelled(true);
                    return;
                }

                ItemStack cursorItem = event.getCursor();
                if (cursorItem != null && plugin.getStoneItemManager().isDiabloStone(cursorItem)) {
                    event.setCancelled(true);

                    StoneType stoneType = plugin.getStoneItemManager().getStoneType(cursorItem);
                    if (stoneType == null) return;

                    event.setCursor(new ItemStack(Material.AIR));
                    player.closeInventory();

                    startAbsorptionAnimation(player, stoneType);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            openAbsorbGuis.remove(event.getPlayer().getUniqueId());
        }
    }

    private void startAbsorptionAnimation(Player player, StoneType stoneType) {
        UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 60;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                ticks++;
                double radius = 1.5 - (ticks * 0.02);
                if (radius < 0.2) radius = 0.2;

                double angle = ticks * 0.4;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                double y = (ticks * 0.03);

                player.getWorld().spawnParticle(
                        Particle.TOTEM_OF_UNDYING,
                        player.getLocation().add(x, y, z),
                        5, 0.05, 0.05, 0.05, 0.05
                );
                player.getWorld().spawnParticle(
                        Particle.FLAME,
                        player.getLocation().add(-x, y, -z),
                        3, 0.05, 0.05, 0.05, 0.02
                );

                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.5f, 0.8f + (ticks / 60.0f));

                if (ticks >= maxTicks) {
                    userData.setAbsorbedStone(stoneType);
                    plugin.getUserManager().savePlayerData(player);
                    player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[DiabloSMP] " +
                            ChatColor.GREEN + "You have successfully absorbed " + stoneType.getDisplayName() + ChatColor.GREEN + " into your body!");
                    player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0f, 1.2f);
                    player.getWorld().spawnParticle(Particle.FLASH, player.getLocation().add(0, 1, 0), 1);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
