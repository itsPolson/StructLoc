package io.github.paulmrtnz;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

public class CompassUpdateTask extends BukkitRunnable {
    private final StructLoc plugin;

    public CompassUpdateTask(StructLoc plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            updatePlayerCompass(player);
        }
    }

    private void updatePlayerCompass(Player player) {
        // Chercher la boussole avec le nom configuré
        ItemStack compass = null;
        String compassName = Config.getCompassName();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.COMPASS) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    String displayName = meta.displayName() != null ? 
                        meta.displayName().toString() : "";
                    if (displayName.contains(compassName)) {
                        compass = item;
                        break;
                    }
                }
            }
        }

        if (compass == null) return;

        // Récupérer la structure sélectionnée du joueur
        String structId = player.getPersistentDataContainer()
            .get(StructLoc.STRUCTURE_KEY, PersistentDataType.STRING);
        if (structId == null) return;

        StructureType type = getStructureById(structId);
        if (type == null) return;

        Location playerLoc = player.getLocation();
        if (playerLoc.getWorld() == null) return;

        // Localiser la structure la plus proche
        var result = playerLoc.getWorld().locateNearestStructure(playerLoc, type, Config.getMaxSearchDistance(), Config.shouldFindUnexplored());
        if (result == null) return;

        // Mettre à jour la boussole pour pointer vers la structure
        ItemMeta meta = compass.getItemMeta();
        if (meta instanceof CompassMeta) {
            CompassMeta compassMeta = (CompassMeta) meta;
            Location structureLoc = result.getLocation();
            compassMeta.setLodestone(structureLoc);
            compassMeta.setLodestoneTracked(false);
            compass.setItemMeta(compassMeta);
        }
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
