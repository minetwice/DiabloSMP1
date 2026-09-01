package com.sdmine.elementalcore.recipes;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import java.util.ArrayList;
import java.util.List;

public class RecipeLoader {
    private final ElementalCorePlugin plugin;
    private final List<NamespacedKey> registeredKeys;

    public RecipeLoader(ElementalCorePlugin plugin) { this.plugin = plugin; this.registeredKeys = new ArrayList<>(); }

    public void loadAllRecipes() {
        ConfigurationSection recipes = plugin.getConfig().getConfigurationSection("recipes");
        if (recipes == null) { plugin.getLogger().warning("No 'recipes' section in config.yml!"); return; }
        for (String id : recipes.getKeys(false)) {
            ConfigurationSection rs = recipes.getConfigurationSection(id);
            if (rs == null || !rs.getBoolean("enabled", true)) continue;
            CoreType type = extractCoreType(id, rs);
            if (type == null) { plugin.getLogger().warning("Cannot determine core type for: " + id); continue; }
            ItemStack result = plugin.getItemFactory().createCore(type);
            NamespacedKey key = new NamespacedKey(plugin, id);
            if ("shapeless".equalsIgnoreCase(rs.getString("type","shaped"))) registerShapeless(rs, key, result, id);
            else registerShaped(rs, key, result, id);
        }
        plugin.getLogger().info("Loaded " + registeredKeys.size() + " recipes.");
    }

    private void registerShaped(ConfigurationSection rs, NamespacedKey key, ItemStack result, String id) {
        List<String> shape = rs.getStringList("shape");
        if (shape.isEmpty()) { plugin.getLogger().warning("Recipe " + id + " has no shape!"); return; }
        ConfigurationSection ing = rs.getConfigurationSection("ingredients");
        if (ing == null) { plugin.getLogger().warning("Recipe " + id + " has no ingredients!"); return; }
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(shape.toArray(new String[0]));
        for (String k : ing.getKeys(false)) { Material m = Material.matchMaterial(ing.getString(k)); if (m != null) recipe.setIngredient(k.charAt(0), m); else plugin.getLogger().warning("Invalid material in " + id); }
        try { plugin.getServer().addRecipe(recipe); registeredKeys.add(key); } catch (Exception e) { plugin.getLogger().warning("Failed to register " + id + ": " + e.getMessage()); }
    }

    private void registerShapeless(ConfigurationSection rs, NamespacedKey key, ItemStack result, String id) {
        List<String> ings = rs.getStringList("ingredients_list");
        if (ings.isEmpty()) { ConfigurationSection is = rs.getConfigurationSection("ingredients"); if (is != null) for (String k : is.getKeys(false)) ings.add(is.getString(k)); }
        if (ings.isEmpty()) { plugin.getLogger().warning("Recipe " + id + " has no ingredients!"); return; }
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        for (String mn : ings) { Material m = Material.matchMaterial(mn); if (m != null) recipe.addIngredient(m); }
        try { plugin.getServer().addRecipe(recipe); registeredKeys.add(key); } catch (Exception e) { plugin.getLogger().warning("Failed to register " + id + ": " + e.getMessage()); }
    }

    private CoreType extractCoreType(String id, ConfigurationSection rs) {
        String ex = rs.getString("result_core"); if (ex != null) { CoreType t = CoreType.fromKey(ex); if (t != null) return t; }
        if (id.startsWith("core_")) return CoreType.fromKey(id.substring(5));
        return null;
    }

    public void removeAllRecipes() { for (NamespacedKey k : registeredKeys) plugin.getServer().removeRecipe(k); registeredKeys.clear(); }
}
