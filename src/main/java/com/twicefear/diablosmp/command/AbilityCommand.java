package com.twicefear.diablosmp.command;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.ability.Ability;
import com.twicefear.diablosmp.util.AbilityItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AbilityCommand implements CommandExecutor, TabCompleter {

    private final DiabloSMP plugin;

    public AbilityCommand(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("abilities")) {
            listAbilities(sender);
            return true;
        }

        if (command.getName().equalsIgnoreCase("ability")) {
            if (args.length == 0) {
                sender.sendMessage(plugin.prefix() + ChatColor.YELLOW + "Usage: /ability give <name>");
                return true;
            }

            if (args[0].equalsIgnoreCase("give")) {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(plugin.prefix() + ChatColor.RED + "This command can only be used by players.");
                    return true;
                }
                if (!player.hasPermission("diablosmp.ability.give")) {
                    player.sendMessage(plugin.prefix() + ChatColor.RED + "You don't have permission.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.prefix() + ChatColor.YELLOW + "Usage: /ability give <name>");
                    return true;
                }
                String abilityName = args[1];
                Ability ability = plugin.getAbilityRegistry().getAbility(abilityName);
                if (ability == null) {
                    player.sendMessage(plugin.prefix() + ChatColor.RED
                            + "Ability not found: " + ChatColor.YELLOW + abilityName);
                    return true;
                }
                ItemStack item = AbilityItem.create(ability);
                player.getInventory().addItem(item);
                player.sendMessage(plugin.prefix() + ChatColor.GREEN
                        + "You received the ability: " + ChatColor.GOLD + ability.getDisplayName());
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                listAbilities(sender);
                return true;
            }
        }
        return true;
    }

    private void listAbilities(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== " + ChatColor.YELLOW + "Available Abilities" + ChatColor.GOLD + " ===");
        for (Ability ability : plugin.getAbilityRegistry().getAllAbilities()) {
            sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + ability.getId()
                    + ChatColor.GRAY + " (" + ability.getElement() + "): " + ChatColor.WHITE + ability.getDescription());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("ability")) {
            if (args.length == 1) {
                completions.add("give");
                completions.add("list");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                for (Ability ability : plugin.getAbilityRegistry().getAllAbilities()) {
                    completions.add(ability.getId());
                }
            }
        }
        return completions;
    }
}
