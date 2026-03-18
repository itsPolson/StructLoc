package io.github.paulmrtnz;

import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StructLocCommand implements CommandExecutor, TabCompleter {

    public StructLocCommand(StructLoc plugin) {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("structloc.command")) {
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage("§cUsage: /structloc <structure>");
            // player.sendMessage("§eStructures: village, stronghold, end_city, bastion_remnant, ancient_city, desert_pyramid, jungle_pyramid, igloo, mineshaft, shipwreck, ocean_ruins, monument, pillager_outpost, swamp_hut, woodland_mansion, fortress, trail_ruins, trial_chambers, buried_treasure, ruined_portal");
            return true;
        }

        String id = args[0].toLowerCase();
        Structure structure = getStructureById(id);
        if (structure == null) {
            player.sendMessage("§cStructure inconnue.");
            return true;
        }

        Location loc = player.getLocation();
        if (loc.getWorld() == null) {
            player.sendMessage("§cErreur: monde invalide.");
            return true;
        }
        
        var result = loc.getWorld().locateNearestStructure(loc, structure, Config.getMaxSearchDistance(), Config.shouldFindUnexplored());
        if (result == null) {
            player.sendMessage("§cAucune " + id + " trouvée.");
            return true;
        }

        player.getPersistentDataContainer().set(StructLoc.STRUCTURE_KEY, PersistentDataType.STRING, id);
        double dist = result.getLocation().distance(loc);
        player.sendMessage("§a" + id + " sélectionnée ! §e" + (int)dist + " blocs");
        return true;
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) return new ArrayList<>();
        
        List<String> structures = new ArrayList<>();
        for (Structure structure : Registry.STRUCTURE) {
            structures.add(structure.getKey().getKey());
        }
        
        String input = args[0].toLowerCase();
        structures.removeIf(s -> !s.startsWith(input));
        return structures;
    }
}
