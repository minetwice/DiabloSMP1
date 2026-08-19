package me.twicefear.diablosmp.ability;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.ability.impl.EarthSmasherAbility;
import me.twicefear.diablosmp.ability.impl.FlameLordAbility;
import me.twicefear.diablosmp.ability.impl.GenericStoneAbility;
import me.twicefear.diablosmp.ability.impl.VoidWalkerAbility;
import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class AbilityListener implements Listener {

    private final DiabloSMP plugin;
    private final EarthSmasherAbility earthSmasher;
    private final FlameLordAbility flameLord;
    private final VoidWalkerAbility voidWalker;
    private final me.twicefear.diablosmp.ability.impl.FrostMonarchAbility frostMonarch;
    private final me.twicefear.diablosmp.ability.impl.LightningOverlordAbility lightningOverlord;
    private final Map<StoneType, GenericStoneAbility> genericAbilities = new HashMap<>();

    public AbilityListener(DiabloSMP plugin) {
        this.plugin = plugin;
        this.earthSmasher = new EarthSmasherAbility(plugin);
        this.flameLord = new FlameLordAbility(plugin);
        this.voidWalker = new VoidWalkerAbility(plugin);
        this.frostMonarch = new me.twicefear.diablosmp.ability.impl.FrostMonarchAbility(plugin);
        this.lightningOverlord = new me.twicefear.diablosmp.ability.impl.LightningOverlordAbility(plugin);

        plugin.getServer().getPluginManager().registerEvents(earthSmasher, plugin);

        for (StoneType type : StoneType.values()) {
            if (type != StoneType.EARTH_SMASHER && type != StoneType.FLAME_LORD && type != StoneType.VOID_WALKER
                    && type != StoneType.FROST_MONARCH && type != StoneType.LIGHTNING_OVERLORD) {
                genericAbilities.put(type, new GenericStoneAbility(plugin, type));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!plugin.getSmpManager().isStarted()) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked != null && clicked.getType().isInteractable()) {
            return;
        }

        Player player = event.getPlayer();
        UserData userData = plugin.getUserManager().getUserData(player.getUniqueId());

        if (!userData.hasAbsorbedStone()) {
            return;
        }

        StoneType stone = userData.getAbsorbedStone();
        boolean isSneaking = player.isSneaking();
        String cooldownKey = stone.getId() + (isSneaking ? "_secondary" : "_primary");

        if (userData.isCooldowned(cooldownKey)) {
            double rem = userData.getRemainingCooldownSeconds(cooldownKey);
            player.sendMessage(ChatColor.RED + "Ability on cooldown! " + String.format("%.1fs", rem) + " remaining.");
            return;
        }

        int cdDuration = isSneaking ? stone.getSecondaryCooldown() : stone.getPrimaryCooldown();
        userData.setCooldown(cooldownKey, cdDuration);

        executeAbilityForStone(player, stone, isSneaking);
    }

    private void executeAbilityForStone(Player player, StoneType stone, boolean isSecondary) {
        switch (stone) {
            case EARTH_SMASHER:
                if (isSecondary) earthSmasher.executeSecondary(player);
                else earthSmasher.execute(player);
                break;
            case FLAME_LORD:
                if (isSecondary) flameLord.executeSecondary(player);
                else flameLord.execute(player);
                break;
            case VOID_WALKER:
                if (isSecondary) voidWalker.executeSecondary(player);
                else voidWalker.execute(player);
                break;
            case FROST_MONARCH:
                if (isSecondary) frostMonarch.executeSecondary(player);
                else frostMonarch.execute(player);
                break;
            case LIGHTNING_OVERLORD:
                if (isSecondary) lightningOverlord.executeSecondary(player);
                else lightningOverlord.execute(player);
                break;
            default:
                GenericStoneAbility generic = genericAbilities.get(stone);
                if (generic != null) {
                    if (isSecondary) generic.executeSecondary(player);
                    else generic.execute(player);
                }
                break;
        }
    }
}
