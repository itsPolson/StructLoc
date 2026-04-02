package net.itspolson.structloc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

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

        Structure structure = getStructureById(structId);
        if (structure == null) return;

        Location playerLoc = player.getLocation();
        if (playerLoc.getWorld() == null) return;

        // Localiser la structure la plus proche
        var result = playerLoc.getWorld().locateNearestStructure(playerLoc, structure, Config.getMaxSearchDistance(), Config.shouldFindUnexplored());
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

    private @Nullable Structure getStructureById(String id) {
        var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE);
        for (Structure structure : registry) {
            String key = registry.getKey(structure).getKey();
            if (key.equalsIgnoreCase(id)) {
                return structure;
            }
        }
        return null;
    }
}
