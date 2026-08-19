package com.twicefear.diablosmp.command;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DSMPCommand implements CommandExecutor, TabCompleter {

    private final DiabloSMP plugin;
    private final Map<UUID, Integer> pendingMinutes = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> pendingSeconds = new ConcurrentHashMap<>();

    public DSMPCommand(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sendHelp(sender); return true; }
        switch (args[0].toLowerCase()) {
            case "start" -> handleStart(sender);
            case "stop" -> handleStop(sender);
            case "changename" -> handleChangeName(sender, args);
            case "withdraw" -> handleWithdraw(sender);
            case "reload" -> handleReload(sender);
            case "status" -> handleStatus(sender);
            case "reset" -> handleReset(sender);
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage(plugin.messages().prefixed("unknown-command"));
        }
        return true;
    }

    private void handleStart(CommandSender sender) {
        if (!sender.hasPermission("diablosmp.admin")) { sender.sendMessage(plugin.messages().prefixed("no-permission")); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.messages().prefixed("player-only")); return; }
        if (!plugin.smp().isIdle()) { sender.sendMessage(plugin.messages().prefixed("smp-already-started")); return; }
        pendingMinutes.put(player.getUniqueId(), plugin.config().graceMinutes());
        pendingSeconds.put(player.getUniqueId(), plugin.config().graceSeconds());
        openStartGUI(player);
    }

    private void openStartGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_RED + "DiabloSMP Timer");
        int mins = pendingMinutes.getOrDefault(player.getUniqueId(), 10);
        int secs = pendingSeconds.getOrDefault(player.getUniqueId(), 0);
        inv.setItem(11, makeItem(Material.CLOCK, ChatColor.YELLOW + "Minutes: " + ChatColor.WHITE + mins, "7Left click +1", "7Right click -1"));
        inv.setItem(13, makeItem(Material.REPEATER, ChatColor.YELLOW + "Seconds: " + ChatColor.WHITE + secs, "7Left click +5", "7Right click -5"));
        inv.setItem(15, makeItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "" + ChatColor.BOLD + "START SMP", "7Minutes: e" + mins, "7Seconds: e" + secs));
        ItemStack fill = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 27; i++) { if (inv.getItem(i) == null) inv.setItem(i, fill); }
        player.openInventory(inv);
    }

    private ItemStack makeItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            for (String l : lore) loreList.add(l);
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void handleGUIClick(Player player, int slot, boolean rightClick) {
        int mins = pendingMinutes.getOrDefault(player.getUniqueId(), 10);
        int secs = pendingSeconds.getOrDefault(player.getUniqueId(), 0);
        switch (slot) {
            case 11 -> { if (rightClick) mins = Math.max(0, mins - 1); else mins = Math.min(120, mins + 1); pendingMinutes.put(player.getUniqueId(), mins); }
            case 13 -> { if (rightClick) secs = Math.max(0, secs - 5); else secs = Math.min(55, secs + 5); pendingSeconds.put(player.getUniqueId(), secs); }
            case 15 -> { player.closeInventory(); plugin.smp().start(mins, secs); return; }
            default -> { return; }
        }
        openStartGUI(player);
    }

    public boolean isStartGUI(String title) {
        return title != null && ChatColor.stripColor(title).startsWith("DiabloSMP");
    }

    private void handleStop(CommandSender sender) {
        if (!sender.hasPermission("diablosmp.admin")) { sender.sendMessage(plugin.messages().prefixed("no-permission")); return; }
        plugin.smp().stop();
    }

    private void handleChangeName(CommandSender sender, String[] args) {
        if (!sender.hasPermission("diablosmp.admin")) { sender.sendMessage(plugin.messages().prefixed("no-permission")); return; }
        if (args.length < 2) { sender.sendMessage("eUsage: /diablosmp changename <name>"); return; }
        String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        plugin.smp().changeName(name);
        sender.sendMessage(plugin.messages().prefixed("name-changed", "name", name));
    }

    private void handleWithdraw(CommandSender sender) {
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.messages().prefixed("player-only")); return; }
        com.twicefear.diablosmp.stone.StoneType stone = plugin.players().getAbsorbedStone(player.getUniqueId());
        if (stone == null) { sender.sendMessage(plugin.messages().prefixed("withdraw-none")); return; }
        plugin.players().setAbsorbedStone(player.getUniqueId(), null);
        player.getInventory().addItem(plugin.stones().createStone(stone));
        player.sendMessage(plugin.messages().prefixed("withdraw-success"));
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("diablosmp.admin")) { sender.sendMessage(plugin.messages().prefixed("no-permission")); return; }
        try { plugin.reload(); sender.sendMessage(plugin.messages().prefixed("reload-success")); }
        catch (Exception e) { sender.sendMessage(plugin.messages().prefixed("reload-fail")); plugin.getLogger().warning("Reload failed: " + e.getMessage()); }
    }

    private void handleStatus(CommandSender sender) {
        String state = switch (plugin.smp().getState()) {
            case IDLE -> plugin.messages().prefixed("smp-idle");
            case GRACE -> "eDiabloSMP is in 6GRACE e(" + plugin.smp().getGraceRemaining() + "s remaining)";
            case RUNNING -> plugin.messages().prefixed("smp-running");
        };
        sender.sendMessage(state);
        sender.sendMessage("7SMP Name: e" + plugin.config().smpName());
    }

    private void handleReset(CommandSender sender) {
        if (!sender.hasPermission("diablosmp.admin")) { sender.sendMessage(plugin.messages().prefixed("no-permission")); return; }
        for (var p : Bukkit.getOnlinePlayers()) plugin.cooldowns().clear(p.getUniqueId());
        sender.sendMessage("aAll cooldowns cleared.");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("8m---------- r c lDiabloSMP 8m----------");
        sender.sendMessage("e/diablosmp start 7- Start the SMP (admin)");
        sender.sendMessage("e/diablosmp stop 7- Stop the SMP (admin)");
        sender.sendMessage("e/diablosmp changename <name> 7- Change SMP name");
        sender.sendMessage("e/diablosmp withdraw 7- Withdraw absorbed stone");
        sender.sendMessage("e/diablosmp reload 7- Reload config (admin)");
        sender.sendMessage("e/diablosmp status 7- Show SMP status");
        sender.sendMessage("e/diablosmp reset 7- Clear all cooldowns (admin)");
        sender.sendMessage("e/diablostone <stone> [player] 7- Give a stone (admin)");
        sender.sendMessage("8m---------- r 7Author: cTwicefear 8m----------");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(List.of("start", "stop", "changename", "withdraw", "reload", "status", "reset", "help"));
            subs.removeIf(s -> !s.toLowerCase().startsWith(args[0].toLowerCase()));
            return subs;
        }
        return new ArrayList<>();
    }
}
