package me.twicefear.diablosmp.ability;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.ability.impl.*;
import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AbilityListener implements Listener {

    private final DiabloSMP plugin;
    private final EarthSmasherAbility earthSmasher;
    private final FlameLordAbility flameLord;
    private final VoidWalkerAbility voidWalker;
    private final FrostMonarchAbility frostMonarch;
    private final LightningOverlordAbility lightningOverlord;
    private final ShadowReaperAbility shadowReaper;
    private final VenomHydraAbility venomHydra;
    private final CelestialWardenAbility celestialWarden;
    private final WindTempestAbility windTempest;
    private final BloodBerserkerAbility bloodBerserker;
    private final GravityMasterAbility gravityMaster;
    private final TimeWeaverAbility timeWeaver;
    private final PhantomAssassinAbility phantomAssassin;
    private final IronTitanAbility ironTitan;
    private final ChaosArchonAbility chaosArchon;

    public AbilityListener(DiabloSMP plugin) {
        this.plugin = plugin;
        this.earthSmasher = new EarthSmasherAbility(plugin);
        this.flameLord = new FlameLordAbility(plugin);
        this.voidWalker = new VoidWalkerAbility(plugin);
        this.frostMonarch = new FrostMonarchAbility(plugin);
        this.lightningOverlord = new LightningOverlordAbility(plugin);
        this.shadowReaper = new ShadowReaperAbility(plugin);
        this.venomHydra = new VenomHydraAbility(plugin);
        this.celestialWarden = new CelestialWardenAbility(plugin);
        this.windTempest = new WindTempestAbility(plugin);
        this.bloodBerserker = new BloodBerserkerAbility(plugin);
        this.gravityMaster = new GravityMasterAbility(plugin);
        this.timeWeaver = new TimeWeaverAbility(plugin);
        this.phantomAssassin = new PhantomAssassinAbility(plugin);
        this.ironTitan = new IronTitanAbility(plugin);
        this.chaosArchon = new ChaosArchonAbility(plugin);

        plugin.getServer().getPluginManager().registerEvents(earthSmasher, plugin);
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
            case SHADOW_REAPER:
                if (isSecondary) shadowReaper.executeSecondary(player);
                else shadowReaper.execute(player);
                break;
            case VENOM_HYDRA:
                if (isSecondary) venomHydra.executeSecondary(player);
                else venomHydra.execute(player);
                break;
            case CELESTIAL_WARDEN:
                if (isSecondary) celestialWarden.executeSecondary(player);
                else celestialWarden.execute(player);
                break;
            case WIND_TEMPEST:
                if (isSecondary) windTempest.executeSecondary(player);
                else windTempest.execute(player);
                break;
            case BLOOD_BERSERKER:
                if (isSecondary) bloodBerserker.executeSecondary(player);
                else bloodBerserker.execute(player);
                break;
            case GRAVITY_MASTER:
                if (isSecondary) gravityMaster.executeSecondary(player);
                else gravityMaster.execute(player);
                break;
            case TIME_WEAVER:
                if (isSecondary) timeWeaver.executeSecondary(player);
                else timeWeaver.execute(player);
                break;
            case PHANTOM_ASSASSIN:
                if (isSecondary) phantomAssassin.executeSecondary(player);
                else phantomAssassin.execute(player);
                break;
            case IRON_TITAN:
                if (isSecondary) ironTitan.executeSecondary(player);
                else ironTitan.execute(player);
                break;
            case CHAOS_ARCHON:
                if (isSecondary) chaosArchon.executeSecondary(player);
                else chaosArchon.execute(player);
                break;
        }
    }
}
