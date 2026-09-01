package com.sdmine.elementalcore.listeners;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
import com.sdmine.elementalcore.items.ItemFactory;
import com.sdmine.elementalcore.socket.SocketManager;
import com.sdmine.elementalcore.variants.ParticleEffectHandler;
import com.sdmine.elementalcore.variants.VariantRegistry;
import com.sdmine.elementalcore.variants.WeaponVariant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Map;

public class PassiveEffectTask extends BukkitRunnable {
    private final ElementalCorePlugin plugin;
    private final ParticleEffectHandler particleHandler;

    public PassiveEffectTask(ElementalCorePlugin plugin) { this.plugin = plugin; this.particleHandler = new ParticleEffectHandler(); }

    @Override
    public void run() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            ItemStack mh = p.getInventory().getItemInMainHand();
            if (!plugin.getItemFactory().isElementalBlade(mh)) continue;
            CoreType[] sockets = plugin.getSocketManager().readSockets(mh);
            WeaponVariant variant = plugin.getVariantRegistry().matchVariant(sockets);
            if (variant == null) continue;
            applyContinuous(p, variant);
            if (!variant.getParticleType().equals("NONE")) particleHandler.emitParticles(p, variant);
        }
    }

    private void applyContinuous(Player p, WeaponVariant v) {
        String t = v.getPassiveType(); Map<String,Object> c = v.getPassiveConfig();
        switch (t) {
            case "WATER_BREATHING_DOLPHIN" -> { apply(p, PotionEffectType.WATER_BREATHING, toI(c.getOrDefault("water_breathing_level",1))-1); apply(p, PotionEffectType.DOLPHINS_GRACE, toI(c.getOrDefault("dolphin_grace_level",1))-1); }
            case "RESISTANCE_KNOCKBACK" -> apply(p, PotionEffectType.DAMAGE_RESISTANCE, toI(c.getOrDefault("resistance_level",2))-1);
            case "ATTACK_SPEED_BURN" -> apply(p, PotionEffectType.FAST_DIGGING, Math.max((int)Math.round(toD(c.getOrDefault("attack_speed_bonus",0.25))*4)-1, 0));
            case "SPEED_JUMP" -> { apply(p, PotionEffectType.SPEED, toI(c.getOrDefault("speed_level",3))-1); apply(p, PotionEffectType.JUMP, toI(c.getOrDefault("jump_level",2))-1); }
            case "REGEN_LIGHT" -> apply(p, PotionEffectType.REGENERATION, toI(c.getOrDefault("regen_level",1))-1);
            case "TRI_BALANCE" -> apply(p, PotionEffectType.SPEED, Math.max((int)Math.round(toD(c.getOrDefault("speed_bonus",0.15))*6)-1, 0));
            default -> {}
        }
    }

    private void apply(Player p, PotionEffectType t, int l) { p.addPotionEffect(new PotionEffect(t, 100, l, true, false, false)); }
    private int toI(Object o) { return (o instanceof Number) ? ((Number)o).intValue() : 0; }
    private double toD(Object o) { return (o instanceof Number) ? ((Number)o).doubleValue() : 0; }
}
