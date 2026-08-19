package com.twicefear.diablosmp.commands;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.gui.StartGUI;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DiabloCommand implements CommandExecutor, TabCompleter {

    private final DiabloSMP plugin;

    public DiabloCommand(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "start" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    // Console can still start with seconds argument
                    int seconds = args.length > 1 ? parseInt(args[1], 300) : plugin.getConfigManager().getGracePeriodDefault();
                    plugin.getSmpManager().startSMP(seconds);
                    sender.sendMessage("§aSMP started with " + seconds + "s grace period!");
                    return true;
                }
                // Open GUI for players
                StartGUI gui = new StartGUI(plugin, player);
                plugin.getGuiListener().registerStartGUI(player, gui);
                gui.open();
            }
            case "stop" -> {
                if (!sender.hasPermission("diablosmp.admin")) return true;
                plugin.getSmpManager().stopSMP();
                sender.sendMessage("§cSMP stopped.");
            }
            case "changename" -> {
                if (!sender.hasPermission("diablosmp.admin")) return true;
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /diablosmp changename <name>");
                    return true;
                }
                String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                plugin.getConfigManager().setSmpName(name);
                sender.sendMessage("§aSMP name changed to: §e" + name);
            }
            case "withdraw" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cOnly players can use this.");
                    return true;
                }
                if (!plugin.getAbsorbManager().hasAbsorbed(player)) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-stone-absorbed"));
                    return true;
                }
                StoneType type = plugin.getAbsorbManager().getAbsorbed(player);
                plugin.getAbsorbManager().removeAbsorbed(player);
                player.getInventory().addItem(plugin.getStoneManager().createStoneItem(type));
                player.sendMessage(plugin.getConfigManager().getMessage("stone-withdrawn"));
            }
            case "give" -> {
                if (!sender.hasPermission("diablosmp.admin")) return true;
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /diablosmp give <player> <stone>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }
                try {
                    StoneType type = StoneType.valueOf(args[2].toUpperCase());
                    target.getInventory().addItem(plugin.getStoneManager().createStoneItem(type));
                    sender.sendMessage("§aGave " + type.getDisplayName() + " to " + target.getName());
                } catch (Exception e) {
                    sender.sendMessage("§cInvalid stone. Use /diablosmp list");
                }
            }
            case "list" -> {
                sender.sendMessage("§6§l=== Diablo Stones ===");
                for (StoneType t : StoneType.values()) {
                    sender.sendMessage(t.getColoredName() + " §7- " + t.name());
                }
            }
            case "reload" -> {
                if (!sender.hasPermission("diablosmp.admin")) return true;
                plugin.getConfigManager().reload();
                sender.sendMessage("§aConfig reloaded!");
            }
            case "info" -> {
                sender.sendMessage("§6§lDiabloSMP v" + plugin.getDescription().getVersion());
                sender.sendMessage("§7Author: Twicefear");
                sender.sendMessage("§7Stones loaded: " + StoneType.values().length);
                sender.sendMessage("§7SMP Started: " + plugin.getSmpManager().isRunning());
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== DiabloSMP Commands ===");
        sender.sendMessage("§e/diablosmp start §7- Open start GUI (or /start <seconds> from console)");
        sender.sendMessage("§e/diablosmp stop §7- Stop the SMP");
        sender.sendMessage("§e/diablosmp changename <name> §7- Change SMP name");
        sender.sendMessage("§e/diablosmp withdraw §7- Withdraw absorbed stone");
        sender.sendMessage("§e/diablosmp give <player> <stone> §7- Give a stone");
        sender.sendMessage("§e/diablosmp list §7- List all stones");
        sender.sendMessage("§e/diablosmp reload §7- Reload config");
        sender.sendMessage("§e/diablosmp info §7- Plugin info");
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("start", "stop", "changename", "withdraw", "give", "list", "reload", "info")
                    .stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return null; // player names
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return Arrays.stream(StoneType.values()).map(Enum::name)
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
