package com.sdmine.elementalcore.listeners;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
import com.sdmine.elementalcore.items.ItemFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class FirstJoinListener implements Listener {
    private final ElementalCorePlugin plugin;
    public FirstJoinListener(ElementalCorePlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (p.hasPlayedBefore()) return;
        ItemFactory f = plugin.getItemFactory();
        if (plugin.getConfig().getBoolean("first_join.give_blade", true)) { p.getInventory().addItem(f.createBaseBlade()); plugin.getLogger().info("Gave blade to: " + p.getName()); }
        if (plugin.getConfig().getBoolean("first_join.give_random_core", true)) { int n = plugin.getConfig().getInt("first_join.random_core_count", 1); for (int i = 0; i < n; i++) { CoreType t = f.getRandomCoreType(); p.getInventory().addItem(f.createCore(t)); } plugin.getLogger().info("Gave " + n + " core(s) to: " + p.getName()); }
    }
}
