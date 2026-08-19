package com.twicefear.diablosmp.manager;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class StoneManager {

    private final DiabloSMP plugin;
    private final NamespacedKey stoneKey;
    private final NamespacedKey activeKey;
    public static final int MODEL_BASE = 80000;

    public StoneManager(DiabloSMP plugin) {
        this.plugin = plugin;
        this.stoneKey = new NamespacedKey(plugin, "diablo_stone_id");
        this.activeKey = new NamespacedKey(plugin, "diablo_stone_active");
    }

    public ItemStack createStone(StoneType type) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setCustomModelData(MODEL_BASE + type.ordinal());
        Component name = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(type.display() + " &r&8Stone");
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("\u00a77A dormant diablo stone.").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("\u00a7ePrimary: \u00a77Right Click").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("\u00a7eSecondary: \u00a77Shift + Right Click").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(type.lore())
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("\u00a78\u00bb \u00a77Shift \u00a763x \u00a77to absorb").decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.getPersistentDataContainer().set(stoneKey, PersistentDataType.STRING, type.id());
        meta.getPersistentDataContainer().set(activeKey, PersistentDataType.BYTE, (byte) 0);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createAbsorbedStone(StoneType type) {
        ItemStack item = createStone(type);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(activeKey, PersistentDataType.BYTE, (byte) 1);
            meta.setEnchantmentGlintOverride(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    public StoneType getStoneType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        String id = item.getItemMeta().getPersistentDataContainer().get(stoneKey, PersistentDataType.STRING);
        if (id == null) return null;
        return StoneType.byId(id).orElse(null);
    }

    public boolean isStone(ItemStack item) { return getStoneType(item) != null; }

    public boolean isAbsorbed(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        Byte b = item.getItemMeta().getPersistentDataContainer().get(activeKey, PersistentDataType.BYTE);
        return b != null && b == 1;
    }

    public NamespacedKey stoneKey() { return stoneKey; }
    public NamespacedKey activeKey() { return activeKey; }

    public Color particleColor(StoneType type) {
        return switch (type) {
            case EARTHQUAKE -> Color.fromRGB(139, 90, 43);
            case INFERNO -> Color.fromRGB(255, 80, 0);
            case TEMPEST -> Color.fromRGB(100, 180, 255);
            case FROSTBITE -> Color.fromRGB(120, 220, 255);
            case SHADOW -> Color.fromRGB(60, 0, 90);
            case HOLY -> Color.fromRGB(255, 250, 180);
            case VOID -> Color.fromRGB(130, 0, 200);
            case NATURE -> Color.fromRGB(60, 200, 80);
            case LIGHTNING -> Color.fromRGB(255, 230, 0);
            case BLOOD -> Color.fromRGB(170, 0, 0);
            case GRAVITY -> Color.fromRGB(40, 80, 200);
            case SOUL -> Color.fromRGB(230, 120, 220);
            case ARCANE -> Color.fromRGB(200, 0, 255);
            case PLAGUE -> Color.fromRGB(90, 140, 0);
            case CHRONOS -> Color.fromRGB(255, 180, 0);
        };
    }
}
