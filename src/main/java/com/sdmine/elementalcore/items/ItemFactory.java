package com.sdmine.elementalcore.items;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
import com.sdmine.elementalcore.socket.SocketManager;
import com.sdmine.elementalcore.util.MessageUtil;
import com.sdmine.elementalcore.variants.VariantRegistry;
import com.sdmine.elementalcore.variants.WeaponVariant;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ItemFactory {
    private final ElementalCorePlugin plugin;
    private final NamespacedKey bladeKey;
    private final NamespacedKey coreKey;
    private final NamespacedKey coreTypeKey;

    public ItemFactory(ElementalCorePlugin plugin) {
        this.plugin = plugin;
        this.bladeKey = new NamespacedKey(plugin, "elemental_blade");
        this.coreKey = new NamespacedKey(plugin, "elemental_core");
        this.coreTypeKey = new NamespacedKey(plugin, "core_type");
    }

    public ItemStack createBaseBlade() {
        String materialName = plugin.getConfig().getString("base_weapon.material", "NETHERITE_SWORD");
        Material material = Material.matchMaterial(materialName);
        if (material == null) material = Material.NETHERITE_SWORD;

        ItemStack blade = new ItemStack(material);
        ItemMeta meta = blade.getItemMeta();
        if (meta == null) return blade;

        meta.setDisplayName(MessageUtil.color(plugin.getConfig().getString("base_weapon.display_name", "&l&6Elemental Modular Blade")));
        int cmd = plugin.getConfig().getInt("base_weapon.custom_model_data", 10001);
        meta.setCustomModelData(cmd);

        List<String> lore = new ArrayList<>();
        lore.addAll(MessageUtil.color(plugin.getConfig().getStringList("base_weapon.lore")));
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(bladeKey, PersistentDataType.BYTE, (byte) 1);
        SocketManager sm = plugin.getSocketManager();
        sm.writeSockets(meta, new CoreType[3]);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        blade.setItemMeta(meta);
        return blade;
    }

    public boolean isElementalBlade(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(bladeKey, PersistentDataType.BYTE);
    }

    public ItemStack createCore(CoreType type) {
        String path = "cores." + type.name().toLowerCase();
        String materialName = plugin.getConfig().getString(path + ".material", type.getDefaultMaterial().name());
        Material material = Material.matchMaterial(materialName);
        if (material == null) material = type.getDefaultMaterial();

        ItemStack core = new ItemStack(material);
        ItemMeta meta = core.getItemMeta();
        if (meta == null) return core;

        meta.setDisplayName(MessageUtil.color(plugin.getConfig().getString(path + ".display_name", type.getChatColor() + "Core")));
        int cmd = plugin.getConfig().getInt(path + ".custom_model_data", 20000);
        meta.setCustomModelData(cmd);
        meta.setLore(MessageUtil.color(plugin.getConfig().getStringList(path + ".lore")));
        meta.getPersistentDataContainer().set(coreKey, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(coreTypeKey, PersistentDataType.STRING, type.name());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        core.setItemMeta(meta);
        return core;
    }

    public boolean isElementalCore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(coreKey, PersistentDataType.BYTE);
    }

    public CoreType getCoreType(ItemStack item) {
        if (!isElementalCore(item)) return null;
        String typeName = item.getItemMeta().getPersistentDataContainer().get(coreTypeKey, PersistentDataType.STRING);
        return CoreType.fromKey(typeName);
    }

    public void updateBladeDisplay(ItemStack blade) {
        if (!isElementalBlade(blade)) return;
        ItemMeta meta = blade.getItemMeta();
        if (meta == null) return;

        SocketManager sm = plugin.getSocketManager();
        CoreType[] sockets = sm.readSockets(meta);
        VariantRegistry vr = plugin.getVariantRegistry();
        WeaponVariant variant = vr.matchVariant(sockets);

        if (variant != null && !variant.getDisplayName().isEmpty())
            meta.setDisplayName(MessageUtil.color(variant.getDisplayName()));
        else
            meta.setDisplayName(MessageUtil.color(plugin.getConfig().getString("base_weapon.display_name")));

        int cmd = (variant != null) ? variant.getCustomModelData() : plugin.getConfig().getInt("base_weapon.custom_model_data", 10001);
        meta.setCustomModelData(cmd);

        List<String> lore = new ArrayList<>();
        lore.add(MessageUtil.color("&7A legendary blade built to hold elemental cores."));
        lore.add(MessageUtil.color(MessageUtil.formatSockets(sockets)));

        if (variant != null) {
            lore.add("");
            if (!variant.getPassiveDescription().isEmpty())
                lore.add(MessageUtil.color("&6&lPassive: &r" + variant.getPassiveDescription()));
            if (!variant.getActiveDescription().isEmpty())
                lore.add(MessageUtil.color("&c&lActive: &r" + variant.getActiveDescription()));
            if (variant.getCooldown() > 0)
                lore.add(MessageUtil.color("&8Cooldown: " + variant.getCooldown() + "s"));
        }
        lore.add("");
        lore.add(MessageUtil.color("&8&oShift + Right-Click to open socket menu."));
        meta.setLore(lore);
        blade.setItemMeta(meta);
    }

    public CoreType getRandomCoreType() {
        CoreType[] types = CoreType.values();
        return types[ThreadLocalRandom.current().nextInt(types.length)];
    }

    public NamespacedKey getBladeKey() { return bladeKey; }
    public NamespacedKey getCoreKey() { return coreKey; }
    public NamespacedKey getCoreTypeKey() { return coreTypeKey; }
}
