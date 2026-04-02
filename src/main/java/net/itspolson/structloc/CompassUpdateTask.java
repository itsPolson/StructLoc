package net.itspolson.structloc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StructureSearchResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

public class CompassUpdateTask extends BukkitRunnable {
    private final StructLoc plugin;
    private static final int PARTICLE_FREQUENCY = 10; // Spawn particles every 10 blocks

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

        Location structureLoc = result.getLocation();
        double dist = structureLoc.distance(playerLoc);

        // Check if player has reached the location (proximity detection)
        if (dist <= Config.getProximityDistance()) {
            player.getPersistentDataContainer().remove(StructLoc.STRUCTURE_KEY);
            player.sendMessage(MessageUtils.colorize("&a✓ Vous avez atteint la destination! La boussole a été réinitialisée."));
            return;
        }

        // Mettre à jour la boussole pour pointer vers la structure
        ItemMeta meta = compass.getItemMeta();
        if (meta instanceof CompassMeta) {
            CompassMeta compassMeta = (CompassMeta) meta;
            compassMeta.setLodestone(structureLoc);
            compassMeta.setLodestoneTracked(false);
            compass.setItemMeta(compassMeta);
        }

        // Spawn particle trail if enabled (only if far enough to be useful)
        if (Config.isParticleTrailEnabled() && dist > 5) {
            spawnParticleTrail(playerLoc, structureLoc);
        }
    }

    private void spawnParticleTrail(Location from, Location to) {
        if (from.getWorld() != to.getWorld()) return;

        Vector direction = to.toVector().subtract(from.toVector());
        double distance = direction.length();
        
        if (distance < 1) return;

        direction.normalize();
        
        // Spawn particles along the line to the structure
        int particleCount = Math.min((int) (distance / PARTICLE_FREQUENCY), 100);
        
        for (int i = 1; i <= particleCount; i++) {
            double ratio = (double) i / particleCount;
            Location particleLoc = from.clone().add(direction.clone().multiply(distance * ratio));
            from.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, particleLoc, 2, 0, 0, 0, 0.1);
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
