package io.github.paulmrtnz;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StructLocCommand implements CommandExecutor, TabCompleter {

    private static final List<String> STRUCTURE_IDS = List.of(
        "ancient_city", "bastion_remnant", "buried_treasure", "desert_pyramid",
        "end_city", "fortress", "igloo", "jungle_pyramid", "mineshaft", "monument",
        "ocean_ruins", "pillager_outpost", "ruined_portal", "shipwreck", "stronghold",
        "swamp_hut", "trail_ruins", "trial_chambers", "village", "woodland_mansion"
    );

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
            return true;
        }

        String id = args[0].toLowerCase();
        StructureType type = getStructureById(id);
        if (type == null) {
            player.sendMessage("§cStructure inconnue: §e" + id + "§c.");
            player.sendMessage("§eStructures disponibles: " + String.join(", ", STRUCTURE_IDS));
            return true;
        }

        Location loc = player.getLocation();
        if (loc.getWorld() == null) {
            player.sendMessage("§cErreur: monde invalide.");
            return true;
        }
        
        var result = loc.getWorld().locateNearestStructure(loc, type, Config.getMaxSearchDistance(), Config.shouldFindUnexplored());
        if (result == null) {
            player.sendMessage("§cAucune " + id + " trouvée.");
            return true;
        }

        player.getPersistentDataContainer().set(StructLoc.STRUCTURE_KEY, PersistentDataType.STRING, id);
        double dist = result.getLocation().distance(loc);
        player.sendMessage("§a" + id + " sélectionnée ! §e" + (int)dist + " blocs");
        return true;
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) return new ArrayList<>();
        
        String input = args[0].toLowerCase();
        List<String> matches = new ArrayList<>(STRUCTURE_IDS);
        matches.removeIf(s -> !s.startsWith(input));
        return matches;
    }
}
