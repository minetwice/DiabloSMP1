package com.diablosmp.plugin.command;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.gui.DiabloMenuGUI;
import com.diablosmp.plugin.gui.StarterGUI;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.PlayerData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

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
            case "help" -> sendHelp(sender);
            case "menu" -> {
                if (!(sender instanceof Player player)) {
                    sendMsg(sender, "player-only");
                    return true;
                }
                DiabloMenuGUI.openMenu(plugin, player);
            }
            case "starter" -> {
                if (!(sender instanceof Player player)) {
                    sendMsg(sender, "player-only");
                    return true;
                }
                StarterGUI.openStarterMenu(plugin, player);
            }
            case "select" -> {
                if (!(sender instanceof Player player)) {
                    sendMsg(sender, "player-only");
                    return true;
                }
                if (args.length < 2) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo select <stone>");
                    return true;
                }
                DiabloStoneType type = DiabloStoneType.fromString(args[1]);
                if (type == null) {
                    sendMsg(sender, "stone-invalid", "{stone}", args[1]);
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
                if (!data.hasStone(type)) {
                    sendMsg(sender, "stone-not-owned", "{stone}", type.name());
                    return true;
                }
                data.setActiveStone(type);
                plugin.getStorageService().savePlayerData(player.getUniqueId(), true);
                plugin.getHudService().updateHud(player);
                sendMsg(sender, "active-stone-set", "{stone}", type.name());
            }
            case "hud" -> {
                if (!(sender instanceof Player player)) {
                    sendMsg(sender, "player-only");
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
                boolean newState = !data.isHudEnabled();
                data.setHudEnabled(newState);
                plugin.getStorageService().savePlayerData(player.getUniqueId(), true);
                if (newState) {
                    sendMsg(sender, "hud-enabled");
                    plugin.getHudService().updateHud(player);
                } else {
                    sendMsg(sender, "hud-disabled");
                }
            }
            case "info" -> {
                if (!(sender instanceof Player player)) {
                    sendMsg(sender, "player-only");
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
                sender.sendMessage(miniMessage.deserialize("<gradient:#FF5555:#AA0000>=== Diablo Profile ===</gradient>"));
                sender.sendMessage(miniMessage.deserialize("<gray>Owned Stones: <white>" + data.getOwnedStones().size() + "/15</white></gray>"));
                sender.sendMessage(miniMessage.deserialize("<gray>Active Stone: <white>" + (data.getActiveStone() != null ? data.getActiveStone().name() : "None") + "</white></gray>"));
                sender.sendMessage(miniMessage.deserialize("<gray>HUD Display: <white>" + (data.isHudEnabled() ? "Enabled" : "Disabled") + "</white></gray>"));
            }

            // Admin Commands
            case "give" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo give <player> <stone|all>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[1]);
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(target.getUniqueId());
                boolean itemMode = plugin.getConfig().getBoolean("stones.item-mode", false);

                if (args[2].equalsIgnoreCase("all")) {
                    for (DiabloStoneType t : DiabloStoneType.values()) {
                        data.addStone(t);
                        if (itemMode) {
                            ItemStack item = plugin.getCastingService().createStoneItem(t);
                            target.getInventory().addItem(item);
                        }
                    }
                    sendMsg(sender, "stone-given", "{stone}", "ALL", "{player}", target.getName());
                } else {
                    DiabloStoneType type = DiabloStoneType.fromString(args[2]);
                    if (type == null) {
                        sendMsg(sender, "stone-invalid", "{stone}", args[2]);
                        return true;
                    }
                    data.addStone(type);
                    if (itemMode) {
                        ItemStack item = plugin.getCastingService().createStoneItem(type);
                        target.getInventory().addItem(item);
                    }
                    sendMsg(sender, "stone-given", "{stone}", type.name(), "{player}", target.getName());
                }
                plugin.getStorageService().savePlayerData(target.getUniqueId(), true);
                plugin.getHudService().updateHud(target);
            }
            case "giveall" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 2) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo giveall <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[1]);
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(target.getUniqueId());
                boolean itemMode = plugin.getConfig().getBoolean("stones.item-mode", false);
                for (DiabloStoneType t : DiabloStoneType.values()) {
                    data.addStone(t);
                    if (itemMode) {
                        ItemStack item = plugin.getCastingService().createStoneItem(t);
                        target.getInventory().addItem(item);
                    }
                }
                plugin.getStorageService().savePlayerData(target.getUniqueId(), true);
                plugin.getHudService().updateHud(target);
                sendMsg(sender, "stone-given", "{stone}", "ALL", "{player}", target.getName());
            }
            case "remove" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo remove <player> <stone|all>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[1]);
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(target.getUniqueId());
                if (args[2].equalsIgnoreCase("all")) {
                    data.getOwnedStones().clear();
                    data.setActiveStone(null);
                    sendMsg(sender, "stone-removed", "{stone}", "ALL", "{player}", target.getName());
                } else {
                    DiabloStoneType type = DiabloStoneType.fromString(args[2]);
                    if (type == null) {
                        sendMsg(sender, "stone-invalid", "{stone}", args[2]);
                        return true;
                    }
                    data.removeStone(type);
                    sendMsg(sender, "stone-removed", "{stone}", type.name(), "{player}", target.getName());
                }
                plugin.getStorageService().savePlayerData(target.getUniqueId(), true);
                plugin.getHudService().updateHud(target);
            }
            case "setactive" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo setactive <player> <stone>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[1]);
                    return true;
                }
                DiabloStoneType type = DiabloStoneType.fromString(args[2]);
                if (type == null) {
                    sendMsg(sender, "stone-invalid", "{stone}", args[2]);
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(target.getUniqueId());
                data.addStone(type);
                data.setActiveStone(type);
                plugin.getStorageService().savePlayerData(target.getUniqueId(), true);
                plugin.getHudService().updateHud(target);
                sendMsg(sender, "active-stone-set", "{stone}", type.name());
            }
            case "resetcooldown" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo resetcooldown <player> <stone|all>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[1]);
                    return true;
                }
                if (args[2].equalsIgnoreCase("all")) {
                    plugin.getCooldownService().resetAllCooldowns(target);
                    sendMsg(sender, "cooldown-reset", "{stone}", "ALL", "{player}", target.getName());
                } else {
                    DiabloStoneType type = DiabloStoneType.fromString(args[2]);
                    if (type == null) {
                        sendMsg(sender, "stone-invalid", "{stone}", args[2]);
                        return true;
                    }
                    plugin.getCooldownService().resetCooldown(target, type);
                    sendMsg(sender, "cooldown-reset", "{stone}", type.name(), "{player}", target.getName());
                }
                plugin.getHudService().updateHud(target);
            }
            case "setcooldown" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 4) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo setcooldown <player> <stone> <seconds>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[1]);
                    return true;
                }
                DiabloStoneType type = DiabloStoneType.fromString(args[2]);
                if (type == null) {
                    sendMsg(sender, "stone-invalid", "{stone}", args[2]);
                    return true;
                }
                try {
                    double seconds = Double.parseDouble(args[3]);
                    plugin.getCooldownService().setCooldown(target, type, seconds);
                    sendMsg(sender, "cooldown-set", "{stone}", type.name(), "{player}", target.getName(), "{seconds}", String.valueOf(seconds));
                    plugin.getHudService().updateHud(target);
                } catch (NumberFormatException e) {
                    sendMsg(sender, "command-usage", "{usage}", "Invalid seconds number format.");
                }
            }
            case "toggle" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo toggle <player> <enabled|disabled>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[1]);
                    return true;
                }
                boolean enable = args[2].equalsIgnoreCase("enabled") || args[2].equalsIgnoreCase("true");
                PlayerData data = plugin.getStorageService().getPlayerData(target.getUniqueId());
                data.setPluginEnabledForPlayer(enable);
                plugin.getStorageService().savePlayerData(target.getUniqueId(), true);
                sender.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() + "<green>Set plugin enabled state for <white>" + target.getName() + "</white> to <white>" + enable + "</white>.</green>"));
            }
            case "firstjoin" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 3 || !args[1].equalsIgnoreCase("reset")) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo firstjoin reset <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[2]);
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(target.getUniqueId());
                data.setFirstJoinClaimed(false);
                plugin.getStorageService().savePlayerData(target.getUniqueId(), true);
                sender.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() + "<green>Reset first-join claim state for <white>" + target.getName() + "</white>.</green>"));
            }
            case "list" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (args.length < 2) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo list <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMsg(sender, "player-not-found", "{player}", args[1]);
                    return true;
                }
                PlayerData data = plugin.getStorageService().getPlayerData(target.getUniqueId());
                sender.sendMessage(miniMessage.deserialize("<gradient:#FF5555:#AA0000>=== " + target.getName() + "'s Diablo Stones ===</gradient>"));
                for (DiabloStoneType t : DiabloStoneType.values()) {
                    boolean owned = data.hasStone(t);
                    boolean isActive = data.getActiveStone() == t;
                    sender.sendMessage(miniMessage.deserialize(" - <color:" + t.getColorHex() + ">" + t.name() + "</color>: " + (owned ? (isActive ? "<green>ACTIVE</green>" : "<yellow>OWNED</yellow>") : "<gray>LOCKED</gray>")));
                }
            }
            case "test" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sendMsg(sender, "player-only");
                    return true;
                }
                if (args.length < 2) {
                    sendMsg(sender, "command-usage", "{usage}", "/diablo test <stone>");
                    return true;
                }
                DiabloStoneType type = DiabloStoneType.fromString(args[1]);
                if (type == null) {
                    sendMsg(sender, "stone-invalid", "{stone}", args[1]);
                    return true;
                }
                plugin.getAbilityManager().castAbility(player, type);
                sender.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() + "<green>Testing ability <white>" + type.name() + "</white>!</green>"));
            }
            case "reload" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                plugin.getConfigManager().loadConfigurations();
                sendMsg(sender, "reloaded");
            }
            case "saveall" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                plugin.getStorageService().saveAll(true);
                sendMsg(sender, "save-complete");
            }
            case "cleanup" -> {
                if (!sender.hasPermission("diablosmp.admin")) {
                    sendMsg(sender, "no-permission");
                    return true;
                }
                plugin.getAbilityManager().cleanupAll();
                sendMsg(sender, "cleanup-complete", "{entities}", "0", "{tasks}", "0");
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize("<gradient:#FF5555:#AA0000>=== DiabloSMP Help ===</gradient>"));
        sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo menu</color> <gray>- Open Diablo Stones menu</gray>"));
        sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo select <stone></color> <gray>- Select active stone</gray>"));
        sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo starter</color> <gray>- Open starter selection</gray>"));
        sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo hud</color> <gray>- Toggle HUD</gray>"));
        sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo info</color> <gray>- View player profile info</gray>"));
        if (sender.hasPermission("diablosmp.admin")) {
            sender.sendMessage(miniMessage.deserialize("<gradient:#FF5555:#AA0000>--- Admin Commands ---</gradient>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo give <player> <stone|all></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo giveall <player></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo remove <player> <stone|all></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo setactive <player> <stone></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo setcooldown <player> <stone> <seconds></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo resetcooldown <player> <stone|all></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo toggle <player> <enabled|disabled></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo firstjoin reset <player></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo list <player></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo test <stone></color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo reload</color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo saveall</color>"));
            sender.sendMessage(miniMessage.deserialize("<color:#FF5555>/diablo cleanup</color>"));
        }
    }

    private void sendMsg(CommandSender sender, String key, String... replacements) {
        String msg = plugin.getConfigManager().getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                msg = msg.replace(replacements[i], replacements[i + 1]);
            }
        }
        sender.sendMessage(miniMessage.deserialize(plugin.getConfigManager().getPrefix() + msg));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>(Arrays.asList("help", "menu", "select", "starter", "hud", "info"));
            if (sender.hasPermission("diablosmp.admin")) {
                list.addAll(Arrays.asList("give", "giveall", "remove", "setactive", "setcooldown", "resetcooldown", "toggle", "firstjoin", "list", "test", "reload", "saveall", "cleanup"));
            }
            return filter(list, args[0]);
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("test")) {
                List<String> stones = new ArrayList<>();
                for (DiabloStoneType type : DiabloStoneType.values()) {
                    stones.add(type.name());
                }
                return filter(stones, args[1]);
            }
            if (args[0].equalsIgnoreCase("firstjoin")) {
                return filter(List.of("reset"), args[1]);
            }
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("giveall") || args[0].equalsIgnoreCase("remove") ||
                    args[0].equalsIgnoreCase("setactive") || args[0].equalsIgnoreCase("setcooldown") || args[0].equalsIgnoreCase("resetcooldown") ||
                    args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("list")) {
                return null;
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("resetcooldown")) {
                List<String> stones = new ArrayList<>();
                stones.add("all");
                for (DiabloStoneType type : DiabloStoneType.values()) {
                    stones.add(type.name());
                }
                return filter(stones, args[2]);
            }
            if (args[0].equalsIgnoreCase("setactive")) {
                List<String> stones = new ArrayList<>();
                for (DiabloStoneType type : DiabloStoneType.values()) {
                    stones.add(type.name());
                }
                return filter(stones, args[2]);
            }
            if (args[0].equalsIgnoreCase("toggle")) {
                return filter(List.of("enabled", "disabled"), args[2]);
            }
            if (args[0].equalsIgnoreCase("firstjoin") && args[1].equalsIgnoreCase("reset")) {
                return null;
            }
        }
        return List.of();
    }

    private List<String> filter(List<String> list, String input) {
        List<String> result = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(input.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }
}
