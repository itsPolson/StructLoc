package io.github.paulmrtnz;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StructureSearchResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CompassListener implements Listener {
    private final Map<String, Long> cooldowns = new HashMap<>();
    private static final String COOLDOWN_KEY = "structloc:last_compass_use";

    public CompassListener(StructLoc plugin) {}

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack result = e.getRecipe().getResult();
        if (result.getType() != Material.COMPASS) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        Component displayName = meta.displayName();
        String displayText = displayName instanceof TextComponent ? ((TextComponent) displayName).content() : "";
        if (!displayText.equals(Config.getCompassName())) return;

        Player player = (Player) e.getWhoClicked();
        if (!player.hasPermission("structloc.craft")) {
            player.sendMessage(MessageUtils.colorize("&cVous n'avez pas la permission de fabriquer cette boussole."));
            e.setCancelled(true);
        }
    }

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
        
        // Check if it's a right-click
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            // Left click - allow vanilla behavior
            return;
        }

        // Check cooldown
        if (!hasCooldownPassed(p)) {
            e.setCancelled(true);
            p.sendMessage(MessageUtils.colorize("&cCooldown... Attendez quelques secondes."));
            return;
        }

        // Check permission
        if (!p.hasPermission("structloc.use")) {
            p.sendMessage(MessageUtils.colorize("&cVous n'avez pas la permission d'utiliser la boussole."));
            e.setCancelled(true);
            return;
        }

        // Handle sneak + right-click: reset compass
        if (p.isSneaking()) {
            p.getPersistentDataContainer().remove(StructLoc.STRUCTURE_KEY);
            p.sendMessage(MessageUtils.colorize("&aBoussole réinitialisée."));
            setCooldown(p);
            e.setCancelled(true);
            return;
        }

        // Handle regular right-click: show direction and distance
        String structId = p.getPersistentDataContainer().get(StructLoc.STRUCTURE_KEY, PersistentDataType.STRING);
        if (structId == null) {
            p.sendMessage(MessageUtils.colorize("&eFais &b/structloc <structure> &epour sélectionner"));
            e.setCancelled(true);
            return;
        }

        Structure structure = getStructureById(structId);
        if (structure == null) {
            p.sendMessage(MessageUtils.colorize("&cStructure invalide."));
            e.setCancelled(true);
            return;
        }

        Location loc = p.getLocation();
        if (loc.getWorld() != null) {
            StructureSearchResult result = loc.getWorld().locateNearestStructure(loc, structure, Config.getMaxSearchDistance(), Config.shouldFindUnexplored());
            if (result != null) {
                double dist = result.getLocation().distance(loc);
                
                // Calculate direction
                Location structLoc = result.getLocation();
                Vector direction = structLoc.toVector().subtract(loc.toVector());
                double angle = Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()));
                
                
                p.sendMessage(MessageUtils.colorize("&a" + structId + " &eDistance: &c" + (int)dist + " blocs"));
                setCooldown(p);
            } else {
                p.sendMessage(MessageUtils.colorize("&c" + structId + " non trouvée."));
            }
        }
        e.setCancelled(true);
    }

    private String getDirectionName(double angle) {
        angle = (angle + 360) % 360;
        if (angle < 22.5 || angle >= 337.5) return "Nord";
        if (angle < 67.5) return "Nord-Est";
        if (angle < 112.5) return "Est";
        if (angle < 157.5) return "Sud-Est";
        if (angle < 202.5) return "Sud";
        if (angle < 247.5) return "Sud-Ouest";
        if (angle < 292.5) return "Ouest";
        if (angle < 337.5) return "Nord-Ouest";
        return "Nord";
    }

    private boolean hasCooldownPassed(Player player) {
        String playerName = player.getName();
        if (!cooldowns.containsKey(playerName)) {
            return true;
        }
        
        long lastUse = cooldowns.get(playerName);
        long cooldownMs = Config.getCompassCooldown() * 1000L;
        return System.currentTimeMillis() - lastUse >= cooldownMs;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getName(), System.currentTimeMillis());
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
