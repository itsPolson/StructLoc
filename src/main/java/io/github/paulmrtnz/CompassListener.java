package io.github.paulmrtnz;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StructureSearchResult;
import org.jetbrains.annotations.Nullable;

public class CompassListener implements Listener {

    public CompassListener(StructLoc plugin) {}

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null || item.getType() != Material.COMPASS) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        Component displayName = meta.displayName();
        String displayText = displayName instanceof TextComponent ? ((TextComponent) displayName).content() : "";
        if (!displayText.equals(Config.getCompassName())) return;

        Player p = e.getPlayer();
        String structId = p.getPersistentDataContainer().get(StructLoc.STRUCTURE_KEY, PersistentDataType.STRING);
        if (structId == null) {
            p.sendMessage("§eFais §b/structloc <structure> §epour sélectionner");
            return;
        }

        StructureType type = getStructureById(structId);
        if (type == null) {
            p.sendMessage("§cStructure invalide.");
            return;
        }

        Location loc = p.getLocation();
        if (loc.getWorld() != null) {
            StructureSearchResult result = loc.getWorld().locateNearestStructure(loc, type, Config.getMaxSearchDistance(), Config.shouldFindUnexplored());
            if (result != null) {
                double dist = result.getLocation().distance(loc);
                p.sendMessage("§aDistance vers §e" + structId + "§a : §e" + (int)dist + " blocs");
            } else {
                p.sendMessage("§c" + structId + " non trouvée.");
            }
        }
        e.setCancelled(true);
    }

    private @Nullable StructureType getStructureById(String id) {
        return switch (id) {
            case "ancient_city" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("ancient_city"));
            case "bastion_remnant" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("bastion_remnant"));
            case "buried_treasure" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("buried_treasure"));
            case "desert_pyramid" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("desert_pyramid"));
            case "end_city" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("end_city"));
            case "fortress" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("fortress"));
            case "igloo" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("igloo"));
            case "jungle_pyramid" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("jungle_pyramid"));
            case "mineshaft" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("mineshaft"));
            case "monument" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("monument"));
            case "ocean_ruins" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("ocean_ruins"));
            case "pillager_outpost" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("pillager_outpost"));
            case "ruined_portal" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("ruined_portal"));
            case "shipwreck" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("shipwreck"));
            case "stronghold" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("stronghold"));
            case "swamp_hut" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("swamp_hut"));
            case "trail_ruins" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("trail_ruins"));
            case "trial_chambers" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("trial_chambers"));
            case "village" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("village"));
            case "woodland_mansion" -> Registry.STRUCTURE_TYPE.get(org.bukkit.NamespacedKey.minecraft("woodland_mansion"));
            default -> null;
        };
    }
}
