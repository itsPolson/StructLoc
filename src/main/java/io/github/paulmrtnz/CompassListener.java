package io.github.paulmrtnz;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;

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
            case "ancient_city" -> StructureType.ANCIENT_CITY;
            case "bastion_remnant" -> StructureType.BASTION_REMNANT;
            case "buried_treasure" -> StructureType.BURIED_TREASURE;
            case "desert_pyramid" -> StructureType.DESERT_PYRAMID;
            case "end_city" -> StructureType.END_CITY;
            case "fortress" -> StructureType.FORTRESS;
            case "igloo" -> StructureType.IGLOO;
            case "jungle_pyramid" -> StructureType.JUNGLE_PYRAMID;
            case "mineshaft" -> StructureType.MINESHAFT;
            case "monument" -> StructureType.MONUMENT;
            case "ocean_ruins" -> StructureType.OCEAN_RUINS;
            case "pillager_outpost" -> StructureType.PILLAGER_OUTPOST;
            case "ruined_portal" -> StructureType.RUINED_PORTAL;
            case "shipwreck" -> StructureType.SHIPWRECK;
            case "stronghold" -> StructureType.STRONGHOLD;
            case "swamp_hut" -> StructureType.SWAMP_HUT;
            case "trail_ruins" -> StructureType.TRAIL_RUINS;
            case "trial_chambers" -> StructureType.TRIAL_CHAMBERS;
            case "village" -> StructureType.VILLAGE;
            case "woodland_mansion" -> StructureType.WOODLAND_MANSION;
            default -> null;
        };
    }
}
