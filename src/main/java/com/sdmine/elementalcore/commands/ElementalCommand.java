package com.sdmine.elementalcore.commands;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
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

public class ElementalCommand implements CommandExecutor, TabCompleter {
    private final ElementalCorePlugin plugin;
    public ElementalCommand(ElementalCorePlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) { help(sender); return true; }
        switch (args[0].toLowerCase()) {
            case "socket" -> socket(sender);
            case "reload" -> reload(sender);
            case "givecore" -> giveCore(sender, args);
            case "giveblade" -> giveBlade(sender, args);
            case "info" -> info(sender);
            default -> help(sender);
        }
        return true;
    }

    private void socket(CommandSender s) {
        if (!(s instanceof Player p)) { s.sendMessage(ChatColor.RED + "Players only."); return; }
        ItemStack mh = p.getInventory().getItemInMainHand();
        if (!plugin.getItemFactory().isElementalBlade(mh)) { p.sendMessage(ChatColor.RED + "Hold an Elemental Modular Blade."); return; }
        plugin.getSocketGUIListener().openGUI(p, mh);
    }

    private void reload(CommandSender s) {
        if (!s.hasPermission("elementalcore.admin")) { s.sendMessage(ChatColor.RED + "No permission."); return; }
        plugin.reloadPlugin(); s.sendMessage(ChatColor.GREEN + "Config reloaded.");
    }

    private void giveCore(CommandSender s, String[] a) {
        if (!s.hasPermission("elementalcore.admin")) { s.sendMessage(ChatColor.RED + "No permission."); return; }
        if (a.length < 2) { s.sendMessage(ChatColor.RED + "Usage: /elemental givecore <type> [player]"); return; }
        CoreType t = CoreType.fromKey(a[1]); if (t == null) { s.sendMessage(ChatColor.RED + "Invalid type."); return; }
        Player tgt = (a.length >= 3) ? Bukkit.getPlayer(a[2]) : (s instanceof Player ? (Player)s : null);
        if (tgt == null) { s.sendMessage(ChatColor.RED + "Player not found."); return; }
        tgt.getInventory().addItem(plugin.getItemFactory().createCore(t));
        s.sendMessage(ChatColor.GREEN + "Gave " + t.getColoredSymbol() + " to " + tgt.getName());
    }

    private void giveBlade(CommandSender s, String[] a) {
        if (!s.hasPermission("elementalcore.admin")) { s.sendMessage(ChatColor.RED + "No permission."); return; }
        Player tgt = (a.length >= 2) ? Bukkit.getPlayer(a[1]) : (s instanceof Player ? (Player)s : null);
        if (tgt == null) { s.sendMessage(ChatColor.RED + "Player not found."); return; }
        tgt.getInventory().addItem(plugin.getItemFactory().createBaseBlade());
        s.sendMessage(ChatColor.GREEN + "Gave blade to " + tgt.getName());
    }

    private void info(CommandSender s) {
        s.sendMessage(ChatColor.GOLD + "===== Elemental Core Weapon System =====");
        s.sendMessage(ChatColor.YELLOW + "Version: " + plugin.getPluginMeta().getVersion());
        s.sendMessage(ChatColor.YELLOW + "EI: " + (plugin.getEiHook().isHooked() ? ChatColor.GREEN + "Hooked" : ChatColor.RED + "Not hooked"));
        s.sendMessage(ChatColor.YELLOW + "Variants: " + plugin.getVariantRegistry().getAllVariants().size());
    }

    private void help(CommandSender s) {
        s.sendMessage(ChatColor.GOLD + "===== Elemental Core Weapon System =====");
        s.sendMessage(ChatColor.YELLOW + "/elemental socket " + ChatColor.GRAY + "- Open socket menu");
        s.sendMessage(ChatColor.YELLOW + "/elemental info " + ChatColor.GRAY + "- Plugin info");
        if (s.hasPermission("elementalcore.admin")) {
            s.sendMessage(ChatColor.YELLOW + "/elemental reload " + ChatColor.GRAY + "- Reload config");
            s.sendMessage(ChatColor.YELLOW + "/elemental givecore <type> " + ChatColor.GRAY + "- Give a core");
            s.sendMessage(ChatColor.YELLOW + "/elemental giveblade " + ChatColor.GRAY + "- Give a blade");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args) {
        List<String> c = new ArrayList<>();
        if (args.length == 1) c.addAll(Arrays.asList("socket","info","reload","givecore","giveblade"));
        else if (args.length == 2 && args[0].equalsIgnoreCase("givecore")) for (CoreType t : CoreType.values()) c.add(t.name().toLowerCase());
        String pref = args[args.length-1].toLowerCase(); c.removeIf(x -> !x.toLowerCase().startsWith(pref));
        return c;
    }
}
