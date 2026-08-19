package com.twicefear.diablosmp.command;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
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

public class DStoneCommand implements CommandExecutor, TabCompleter {

    private final DiabloSMP plugin;

    public DStoneCommand(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("diablosmp.admin")) { sender.sendMessage(plugin.messages().prefixed("no-permission")); return true; }
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) { listStones(sender); return true; }
        StoneType type = StoneType.byId(args[0]).orElse(null);
        if (type == null) { sender.sendMessage(plugin.messages().prefixed("stone-unknown", "name", args[0])); return true; }
        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) { sender.sendMessage("cPlayer not found: " + args[1]); return true; }
        } else {
            if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().prefixed("player-only")); return true; }
            target = p;
        }
        target.getInventory().addItem(plugin.stones().createStone(type));
        sender.sendMessage(plugin.messages().prefixed("stone-given", "stone", type.display(), "player", target.getName()));
        if (!sender.equals(target)) { target.sendMessage(plugin.messages().prefixed("stone-received", "stone", type.display())); }
        return true;
    }

    private void listStones(CommandSender sender) {
        sender.sendMessage("8m---- r c lDiablo Stones 8m----");
        for (StoneType st : StoneType.values()) { sender.sendMessage("8- e" + st.id() + " 7- " + st.display()); }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> all = Arrays.stream(StoneType.values()).map(StoneType::id).collect(Collectors.toCollection(ArrayList::new));
            all.add("list");
            all.removeIf(s -> !s.toLowerCase().startsWith(args[0].toLowerCase()));
            return all;
        }
        if (args.length == 2) {
            List<String> players = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) { if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) players.add(p.getName()); }
            return players;
        }
        return new ArrayList<>();
    }
}
