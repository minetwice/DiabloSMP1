package me.twicefear.diablosmp.command;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiabloCommand implements CommandExecutor, TabCompleter {

    private final DiabloSMP plugin;

    private final me.twicefear.diablosmp.smp.StartGUIManager startGuiManager;

    public DiabloCommand(DiabloSMP plugin, me.twicefear.diablosmp.smp.StartGUIManager startGuiManager) {
        this.plugin = plugin;
        this.startGuiManager = startGuiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "start":
                if (!sender.hasPermission("diablosmp.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission.");
                    return true;
                }
                if (!(sender instanceof Player p)) {
                    sender.sendMessage(ChatColor.RED + "Only players can open the start GUI.");
                    return true;
                }
                startGuiManager.openGUI(p);
                break;

            case "withdraw":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                    return true;
                }
                handleWithdraw(player);
                break;

            case "changename":
                if (!sender.hasPermission("diablosmp.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /diablosmp changename <SMP Name>");
                    return true;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(i == args.length - 1 ? "" : " ");
                }
                String newName = sb.toString();
                plugin.getConfig().set("smp.name", newName);
                plugin.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "SMP Name changed to: " + ChatColor.translateAlternateColorCodes('&', newName));
                break;

            case "give":
                if (!sender.hasPermission("diablosmp.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /diablosmp give <player> <stone_id>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
                StoneType type = StoneType.fromId(args[2]);
                if (type == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid stone ID. Valid ones: " + getStoneIds());
                    return true;
                }
                ItemStack stoneItem = plugin.getStoneItemManager().createStoneItem(type);
                target.getInventory().addItem(stoneItem);
                sender.sendMessage(ChatColor.GREEN + "Gave " + type.getDisplayName() + ChatColor.GREEN + " to " + target.getName());
                target.sendMessage(ChatColor.GREEN + "You received " + type.getDisplayName() + ChatColor.GREEN + "!");
                break;

            case "reset":
                if (!sender.hasPermission("diablosmp.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /diablosmp reset <player>");
                    return true;
                }
                Player rTarget = Bukkit.getPlayer(args[1]);
                if (rTarget == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
                UserData uData = plugin.getUserManager().getUserData(rTarget.getUniqueId());
                uData.setAbsorbedStone(null);
                sender.sendMessage(ChatColor.GREEN + "Reset Diablo Stone for " + rTarget.getName());
                rTarget.sendMessage(ChatColor.YELLOW + "Your absorbed Diablo Stone has been reset by an admin.");
                break;

            case "reload":
                if (!sender.hasPermission("diablosmp.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission.");
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "DiabloSMP configuration reloaded.");
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleWithdraw(Player player) {
        UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());
        if (!userData.hasAbsorbedStone()) {
            player.sendMessage(ChatColor.RED + "You do not have any absorbed Diablo Stone to withdraw!");
            return;
        }

        StoneType stone = userData.getAbsorbedStone();
        ItemStack stoneItem = plugin.getStoneItemManager().createStoneItem(stone);

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Your inventory is full! Make space before withdrawing your stone.");
            return;
        }

        userData.setAbsorbedStone(null);
        player.getInventory().addItem(stoneItem);
        player.sendMessage(ChatColor.GREEN + "Successfully withdrew " + stone.getDisplayName() + ChatColor.GREEN + " to your inventory!");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "=== DiabloSMP Commands ===");
        sender.sendMessage(ChatColor.RED + "/diablosmp withdraw " + ChatColor.GRAY + "- Withdraw your absorbed stone");
        sender.sendMessage(ChatColor.RED + "/diablosmp start " + ChatColor.GRAY + "- Open SMP start GUI (Admin)");
        sender.sendMessage(ChatColor.RED + "/diablosmp changename <name> " + ChatColor.GRAY + "- Change SMP name (Admin)");
        sender.sendMessage(ChatColor.RED + "/diablosmp give <player> <stone> " + ChatColor.GRAY + "- Give stone to player (Admin)");
        sender.sendMessage(ChatColor.RED + "/diablosmp reset <player> " + ChatColor.GRAY + "- Reset absorbed stone (Admin)");
        sender.sendMessage(ChatColor.RED + "/diablosmp reload " + ChatColor.GRAY + "- Reload config (Admin)");
    }

    private String getStoneIds() {
        StringBuilder sb = new StringBuilder();
        for (StoneType st : StoneType.values()) {
            sb.append(st.getId()).append(", ");
        }
        return sb.toString();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(Arrays.asList("withdraw", "start", "changename", "give", "reset", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (StoneType st : StoneType.values()) list.add(st.getId());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
        }
        return list;
    }
}
