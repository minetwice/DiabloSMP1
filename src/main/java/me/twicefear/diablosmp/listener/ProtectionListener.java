package me.twicefear.diablosmp.listener;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProtectionListener implements Listener {

    private final DiabloSMP plugin;

    public ProtectionListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (plugin.getStoneItemManager().isDiabloStone(item)) {
            boolean allowDrop = plugin.getConfig().getBoolean("protection.allow_drop_from_inventory", false);
            if (!allowDrop) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Diablo Stones cannot be dropped from your inventory!");
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        boolean isAbsorbGui = event.getView().getTitle().contains("Place Stone to Absorb");
        boolean isContainerOpen = !isAbsorbGui && (topInv.getHolder() != event.getWhoClicked());
        boolean allowStore = plugin.getConfig().getBoolean("protection.allow_store_in_chests", false);

        if (isContainerOpen && !allowStore) {
            ItemStack current = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

            boolean isCurrentStone = plugin.getStoneItemManager().isDiabloStone(current);
            boolean isCursorStone = plugin.getStoneItemManager().isDiabloStone(cursor);

            if (isCurrentStone || isCursorStone) {
                if (event.getClickedInventory() == topInv) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Diablo Stones cannot be stored in containers!");
                } else if (event.isShiftClick()) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Diablo Stones cannot be stored in containers!");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());

        if (userData.hasAbsorbedStone()) {
            boolean dropOnDeath = plugin.getConfig().getBoolean("protection.drop_on_death", true);
            if (dropOnDeath) {
                StoneType stone = userData.getAbsorbedStone();
                userData.setAbsorbedStone(null);
                plugin.getUserManager().savePlayerData(player);
                ItemStack stoneItem = plugin.getStoneItemManager().createStoneItem(stone);
                event.getDrops().add(stoneItem);
                player.sendMessage(ChatColor.DARK_RED + "Your absorbed " + stone.getDisplayName() + ChatColor.DARK_RED + " was dropped on death!");
            }
        }
    }
}
