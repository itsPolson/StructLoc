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
import org.bukkit.generator.structure.Structure;
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

        Structure structure = getStructureById(structId);
        if (structure == null) {
            p.sendMessage("§cStructure invalide.");
            return;
        }

        Location loc = p.getLocation();
        if (loc.getWorld() != null) {
            StructureSearchResult result = loc.getWorld().locateNearestStructure(loc, structure, Config.getMaxSearchDistance(), Config.shouldFindUnexplored());
            if (result != null) {
                double dist = result.getLocation().distance(loc);
                p.sendMessage("§aDistance vers §e" + structId + "§a : §e" + (int)dist + " blocs");
            } else {
                p.sendMessage("§c" + structId + " non trouvée.");
            }
        }
        e.setCancelled(true);
    }

    private @Nullable Structure getStructureById(String id) {
        for (Structure structure : Registry.STRUCTURE) {
            String key = structure.getKey().getKey();
            if (key.equalsIgnoreCase(id)) {
                return structure;
            }
        }
        return null;
    }
}
