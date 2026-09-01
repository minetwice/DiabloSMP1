package com.sdmine.elementalcore.integration;

import com.sdmine.elementalcore.ElementalCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import java.util.Optional;
import java.util.logging.Level;

public class EIHook {
    private boolean hooked;
    private Object executableItemsManager;

    public void hook() {
        Plugin ei = Bukkit.getPluginManager().getPlugin("ExecutableItems");
        if (ei == null || !ei.isEnabled()) { ElementalCorePlugin.getInstance().getLogger().info("ExecutableItems not found — standalone mode."); hooked = false; return; }
        try {
            Class<?> api = Class.forName("com.ssomar.score.api.executableitems.ExecutableItemsAPI");
            this.executableItemsManager = api.getMethod("getExecutableItemsManager").invoke(null);
            this.hooked = true;
            ElementalCorePlugin.getInstance().getLogger().info("ExecutableItems hooked (v" + ei.getPluginMeta().getVersion() + ")!");
        } catch (Exception e) { ElementalCorePlugin.getInstance().getLogger().info("ExecutableItems API not found — standalone mode."); hooked = false; }
    }

    public boolean isHooked() { return hooked; }

    public boolean isExecutableItem(ItemStack item, String id) {
        if (!hooked || executableItemsManager == null) return false;
        try {
            Class<?> mc = Class.forName("com.ssomar.score.api.executableitems.ExecutableItemsManagerInterface");
            Optional<?> r = (Optional<?>) mc.getMethod("getExecutableItem", ItemStack.class).invoke(executableItemsManager, item);
            if (r.isPresent()) { Class<?> ec = Class.forName("com.ssomar.score.api.executableitems.ExecutableItemInterface"); return id.equals(ec.getMethod("getId").invoke(r.get())); }
        } catch (Exception ignored) {}
        return false;
    }

    public Object getExecutableItemsManager() { return executableItemsManager; }
}
