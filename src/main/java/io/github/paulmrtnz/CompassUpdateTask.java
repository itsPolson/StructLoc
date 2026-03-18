package io.github.paulmrtnz;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
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
