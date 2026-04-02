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
    private final StructLoc plugin;

    public StructLocCommand(StructLoc plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        
        // Handle reload command
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("structloc.command.reload")) {
                player.sendMessage(MessageUtils.colorize("&cVous n'avez pas la permission d'utiliser cette commande."));
                return true;
            }
            Config.reload();
            player.sendMessage(MessageUtils.colorize("&aConfiguration rechargée !"));
            return true;
        }

        // Handle structure selection
        if (args.length != 1) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /" + label + " <structure> ou /" + label + " reload"));
            return true;
        }

        if (!player.hasPermission("structloc.use")) {
            player.sendMessage(MessageUtils.colorize("&cVous n'avez pas la permission d'utiliser cette commande."));
            return true;
        }

        String id = args[0].toLowerCase();
        Structure structure = getStructureById(id);
        if (structure == null) {
            player.sendMessage(MessageUtils.colorize("&cStructure inconnue."));
            return true;
        }

        Location loc = player.getLocation();
        if (loc.getWorld() == null) {
            player.sendMessage(MessageUtils.colorize("&cErreur: monde invalide."));
            return true;
        }
        
        var result = loc.getWorld().locateNearestStructure(loc, structure, Config.getMaxSearchDistance(), Config.shouldFindUnexplored());
        if (result == null) {
            player.sendMessage(MessageUtils.colorize("&cAucune " + id + " trouvée."));
            return true;
        }

        player.getPersistentDataContainer().set(StructLoc.STRUCTURE_KEY, PersistentDataType.STRING, id);
        double dist = result.getLocation().distance(loc);
        player.sendMessage(MessageUtils.colorize("&a" + id + " sélectionnée ! &e" + (int)dist + " blocs"));
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
        
        List<String> completions = new ArrayList<>();
        
        // Add reload option if player has permission
        if (sender.hasPermission("structloc.command.reload")) {
            completions.add("reload");
        }
        
        // Add structure names
        for (Structure structure : Registry.STRUCTURE) {
            completions.add(structure.getKey().getKey());
        }
        
        String input = args[0].toLowerCase();
        completions.removeIf(s -> !s.startsWith(input));
        return completions;
    }
}
