package net.itspolson.structloc;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class CompassGiveCommand implements CommandExecutor {

    public CompassGiveCommand(StructLoc plugin) {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("structloc.command.give")) {
            sender.sendMessage(MessageUtils.colorize("&cVous n'avez pas la permission d'utiliser cette commande."));
            return true;
        }

        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.colorize("&cVous devez spécifier un joueur si vous n'êtes pas un joueur."));
                return true;
            }
            target = (Player) sender;
        } else {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtils.colorize("&cJoueur " + args[0] + " introuvable."));
                return true;
            }
        }

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(Config.getCompassName()));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            compass.setItemMeta(meta);
        }

        target.getInventory().addItem(compass);
        sender.sendMessage(MessageUtils.colorize("&aBoussole donnée à &e" + target.getName()));
        if (!sender.equals(target)) {
            target.sendMessage(MessageUtils.colorize("&aVous avez reçu une boussole de localisation !"));
        }
        return true;
    }
}
