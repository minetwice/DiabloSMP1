package com.sdmine.elementalcore.listeners;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
import com.sdmine.elementalcore.gui.SocketGUIListener;
import com.sdmine.elementalcore.items.ItemFactory;
import com.sdmine.elementalcore.socket.SocketManager;
import com.sdmine.elementalcore.variants.AbilityExecutor;
import com.sdmine.elementalcore.variants.PassiveEffectHandler;
import com.sdmine.elementalcore.variants.VariantRegistry;
import com.sdmine.elementalcore.variants.WeaponVariant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponUseListener implements Listener {
    private final ElementalCorePlugin plugin;
    private final AbilityExecutor abilityExecutor;
    private final PassiveEffectHandler passiveHandler;

    public WeaponUseListener(ElementalCorePlugin plugin) { this.plugin = plugin; this.abilityExecutor = new AbilityExecutor(plugin); this.passiveHandler = new PassiveEffectHandler(plugin); }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!plugin.getItemFactory().isElementalBlade(item)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        event.setCancelled(true);
        if (p.isSneaking()) { plugin.getSocketGUIListener().openGUI(p, item); return; }
        SocketManager sm = plugin.getSocketManager();
        CoreType[] sockets = sm.readSockets(item);
        WeaponVariant variant = plugin.getVariantRegistry().matchVariant(sockets);
        if (variant == null || !variant.hasActiveAbility()) { p.sendMessage("§7§l[ECW] §cNo active ability."); return; }
        abilityExecutor.execute(variant, p);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)) return;
        Player p = (Player) event.getDamager();
        LivingEntity t = (LivingEntity) event.getEntity();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!plugin.getItemFactory().isElementalBlade(item)) return;
        WeaponVariant variant = plugin.getVariantRegistry().matchVariant(plugin.getSocketManager().readSockets(item));
        if (variant == null) return;
        passiveHandler.applyOnHitPassive(variant, p, t, event.getDamage());
    }
}
