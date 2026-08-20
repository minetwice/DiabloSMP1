package me.twicefear.diablosmp.listener;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbsorptionListener implements Listener {

    private final DiabloSMP plugin;
    public static final String GUI_TITLE = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Place Stone to Absorb";
    private final Map<UUID, Inventory> openAbsorbGuis = new HashMap<>();

    public static class AbsorbGuiHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public AbsorptionListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();

        // If SMP is not started, allow admins/OPs to absorb for testing, but warn non-OP players
        if (!plugin.getSmpManager().isStarted() && !player.isOp() && !player.hasPermission("diablosmp.admin")) {
            player.sendActionBar(ChatColor.RED + "SMP has not started yet! Admin must run /diablosmp start.");
            return;
        }

        UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());

        if (userData.isFirstJoinAnimationActive()) return;

        // Check if player already has an absorbed stone
        if (userData.hasAbsorbedStone()) {
            player.sendActionBar(ChatColor.RED + "You already have an absorbed stone! Use /diablosmp withdraw first.");
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        boolean mainIsStone = plugin.getStoneItemManager().isDiabloStone(mainHand);
        boolean offIsStone = plugin.getStoneItemManager().isDiabloStone(offHand);

        if (!mainIsStone && !offIsStone) {
            userData.resetShiftCount();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - userData.getLastShiftTime() > 3000) { // 3 seconds timeout
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
        Inventory gui = Bukkit.createInventory(new AbsorbGuiHolder(), 9, GUI_TITLE);

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

        boolean isAbsorbGui = event.getInventory().getHolder() instanceof AbsorbGuiHolder;
        if (!isAbsorbGui && event.getView().getTitle().equals(GUI_TITLE)) {
            isAbsorbGui = true;
        }

        if (!isAbsorbGui) return;

        int rawSlot = event.getRawSlot();

        // If clicking top inventory (slot 0..8)
        if (rawSlot >= 0 && rawSlot < 9) {
            if (rawSlot != 4) {
                event.setCancelled(true);
                return;
            }

            // Slot 4 clicked
            ItemStack cursorItem = event.getCursor();
            if (cursorItem != null && plugin.getStoneItemManager().isDiabloStone(cursorItem)) {
                event.setCancelled(true);
                StoneType stoneType = plugin.getStoneItemManager().getStoneType(cursorItem);
                if (stoneType != null) {
                    event.setCursor(new ItemStack(Material.AIR));
                    player.closeInventory();
                    startAbsorptionAnimation(player, stoneType);
                }
                return;
            }

            ItemStack currentItem = event.getCurrentItem();
            if (currentItem != null && plugin.getStoneItemManager().isDiabloStone(currentItem)) {
                event.setCancelled(true);
                StoneType stoneType = plugin.getStoneItemManager().getStoneType(currentItem);
                if (stoneType != null) {
                    event.setCurrentItem(new ItemStack(Material.AIR));
                    player.closeInventory();
                    startAbsorptionAnimation(player, stoneType);
                }
                return;
            }

            event.setCancelled(true);
        } else if (event.isShiftClick()) {
            // Shift clicking from bottom inventory into GUI
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && plugin.getStoneItemManager().isDiabloStone(clickedItem)) {
                event.setCancelled(true);
                StoneType stoneType = plugin.getStoneItemManager().getStoneType(clickedItem);
                if (stoneType != null) {
                    event.setCurrentItem(new ItemStack(Material.AIR));
                    player.closeInventory();
                    startAbsorptionAnimation(player, stoneType);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        boolean isAbsorbGui = event.getInventory().getHolder() instanceof AbsorbGuiHolder;
        if (isAbsorbGui || event.getView().getTitle().equals(GUI_TITLE)) {
            openAbsorbGuis.remove(event.getPlayer().getUniqueId());
        }
    }

    private void startAbsorptionAnimation(Player player, StoneType stoneType) {
        UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());
        Color pColor = getStoneParticleColor(stoneType);

        // Spawn ItemDisplay for stone floating & orbiting
        ItemStack stoneItem = plugin.getStoneItemManager().createStoneItem(stoneType);
        Location startLoc = player.getLocation().add(0, 0.8, 0);
        ItemDisplay itemDisplay = player.getWorld().spawn(startLoc, ItemDisplay.class, display -> {
            display.setItemStack(stoneItem);
            display.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(),
                    new Vector3f(0.5f, 0.5f, 0.5f),
                    new Quaternionf()
            ));
        });

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 100; // 5 seconds total animation

            @Override
            public void run() {
                if (!player.isOnline() || itemDisplay.isDead()) {
                    if (!itemDisplay.isDead()) itemDisplay.remove();
                    cancel();
                    return;
                }

                ticks++;

                if (ticks <= 60) {
                    // Phase 1: Stone orbits player in a rising spiral out of hand to above head
                    double progress = ticks / 60.0;
                    double radius = 1.2 * (1.0 - (progress * 0.5));
                    double angle = ticks * 0.3;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    double y = 0.8 + (progress * 1.4); // Moves from 0.8 to 2.2 height (above head)

                    Location currentLoc = player.getLocation().add(x, y, z);
                    itemDisplay.teleport(currentLoc);

                    // Particle swirl
                    player.getWorld().spawnParticle(
                            Particle.DUST,
                            currentLoc,
                            3, 0.05, 0.05, 0.05,
                            new Particle.DustOptions(pColor, 1.2f)
                    );
                    player.getWorld().spawnParticle(
                            Particle.END_ROD,
                            currentLoc,
                            1, 0.02, 0.02, 0.02, 0.01
                    );

                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.5f, (float) (0.8 + (progress * 0.8)));

                } else if (ticks <= 90) {
                    // Phase 2: Stone rests above player's head and forms the glowing Angel Head Ring / Halo
                    Location headTop = player.getLocation().add(0, 2.25, 0);
                    itemDisplay.teleport(headTop);

                    // Draw Angel Head Ring (Halo) around head with stone-specific particle colors
                    double ringRadius = 0.55;
                    for (int i = 0; i < 16; i++) {
                        double ringAngle = (i / 16.0) * 2 * Math.PI + (ticks * 0.1);
                        double rx = ringRadius * Math.cos(ringAngle);
                        double rz = ringRadius * Math.sin(ringAngle);
                        player.getWorld().spawnParticle(
                                Particle.DUST,
                                headTop.clone().add(rx, 0, rz),
                                1, 0, 0, 0,
                                new Particle.DustOptions(pColor, 1.4f)
                        );
                    }
                    player.getWorld().spawnParticle(
                            Particle.GLOW,
                            headTop,
                            2, 0.2, 0.05, 0.2, 0.02
                    );

                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.6f, 1.5f);

                } else {
                    // Phase 3: Final absorption flash and crown halo burst
                    itemDisplay.remove();
                    userData.setAbsorbedStone(stoneType);
                    plugin.getUserManager().savePlayerData(player);

                    Location headTop = player.getLocation().add(0, 2.25, 0);
                    player.getWorld().spawnParticle(Particle.FLASH, headTop, 1);
                    player.getWorld().spawnParticle(
                            Particle.DUST,
                            headTop,
                            40, 0.6, 0.2, 0.6,
                            new Particle.DustOptions(pColor, 2.0f)
                    );

                    player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[DiabloSMP] " +
                            ChatColor.GREEN + "You have successfully absorbed " + stoneType.getDisplayName() + ChatColor.GREEN + " into your body!");
                    player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0f, 1.2f);
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.9f, 1.1f);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private Color getStoneParticleColor(StoneType type) {
        return switch (type) {
            case EARTH_SMASHER -> Color.fromRGB(180, 100, 30);
            case FLAME_LORD -> Color.fromRGB(230, 40, 20);
            case VOID_WALKER -> Color.fromRGB(120, 20, 200);
            case FROST_MONARCH -> Color.fromRGB(60, 200, 245);
            case LIGHTNING_OVERLORD -> Color.fromRGB(250, 230, 40);
            case SHADOW_REAPER -> Color.fromRGB(80, 80, 90);
            case VENOM_HYDRA -> Color.fromRGB(30, 180, 50);
            case CELESTIAL_WARDEN -> Color.fromRGB(255, 215, 80);
            case WIND_TEMPEST -> Color.fromRGB(220, 240, 255);
            case BLOOD_BERSERKER -> Color.fromRGB(170, 0, 30);
            case GRAVITY_MASTER -> Color.fromRGB(190, 80, 220);
            case TIME_WEAVER -> Color.fromRGB(40, 110, 230);
            case PHANTOM_ASSASSIN -> Color.fromRGB(100, 110, 120);
            case IRON_TITAN -> Color.fromRGB(0, 160, 180);
            case CHAOS_ARCHON -> Color.fromRGB(240, 50, 180);
        };
    }
}
