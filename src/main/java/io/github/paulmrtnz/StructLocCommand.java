package io.github.paulmrtnz;

import org.bukkit.Location;
import org.bukkit.Registry;
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
        StructureType type = getStructureById(id);
        if (type == null) {
            player.sendMessage("§cStructure inconnue.");
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) return new ArrayList<>();
        
        List<String> structures = new ArrayList<>();
        structures.add("ancient_city");
        structures.add("bastion_remnant");
        structures.add("buried_treasure");
        structures.add("desert_pyramid");
        structures.add("end_city");
        structures.add("fortress");
        structures.add("igloo");
        structures.add("jungle_pyramid");
        structures.add("mineshaft");
        structures.add("monument");
        structures.add("ocean_ruins");
        structures.add("pillager_outpost");
        structures.add("ruined_portal");
        structures.add("shipwreck");
        structures.add("stronghold");
        structures.add("swamp_hut");
        structures.add("trail_ruins");
        structures.add("trial_chambers");
        structures.add("village");
        structures.add("woodland_mansion");
        
        String input = args[0].toLowerCase();
        structures.removeIf(s -> !s.startsWith(input));
        return structures;
    }
}
