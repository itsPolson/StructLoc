package net.itspolson.structloc;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class StructLoc extends JavaPlugin {
    public static final NamespacedKey STRUCTURE_KEY = new NamespacedKey("structloc", "selected_structure");

    @Override
    public void onEnable() {
        // Charger la configuration
        Config.load(this);
        
        getServer().getPluginManager().registerEvents(new CompassListener(this), this);
        StructLocCommand structLocCmd = new StructLocCommand(this);
        getCommand("structloc").setExecutor(structLocCmd);
        getCommand("structloc").setTabCompleter(structLocCmd);
        getCommand("givestructloc").setExecutor(new CompassGiveCommand(this));
        
        // Enregistrer la recette de craft
        CompassRecipe.register(this);
        
        // Démarrer la tâche de mise à jour des boussoles
        new CompassUpdateTask(this).runTaskTimer(this, 0, Config.getCompassUpdateInterval());
        
        getLogger().info("StructLoc activé !");
    }
}
