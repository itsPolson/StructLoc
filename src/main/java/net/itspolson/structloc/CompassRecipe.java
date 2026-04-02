package net.itspolson.structloc;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CompassRecipe {
    public static void register(Plugin plugin) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(Config.getCompassName()));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            compass.setItemMeta(meta);
        }

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "localiseur"), compass);
        recipe.shape("RIR", "IDI", "RIR");
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('D', Material.DIAMOND);

        plugin.getServer().addRecipe(recipe);
    }
}
