
package org.servertools.opsentinel.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class LagAnalyzer {

    private final Plugin plugin;

    public LagAnalyzer(Plugin plugin) {
        this.plugin = plugin;
    }

    public void runAnalysis() {
        plugin.getLogger().info("===== LAG-ANALYSE =====");

        int totalEntities = 0;
        int loadedChunks = 0;

        Map<String, Integer> entityPerWorld = new HashMap<>();

        for (World world : Bukkit.getWorlds()) {
            int entities = world.getEntities().size();
            int chunks = world.getLoadedChunks().length;

            totalEntities += entities;
            loadedChunks += chunks;
            entityPerWorld.put(world.getName(), entities);
        }

        plugin.getLogger().info("Geladene Chunks: " + loadedChunks);
        plugin.getLogger().info("Gesamte Entities: " + totalEntities);

        for (Map.Entry<String, Integer> entry : entityPerWorld.entrySet()) {
            plugin.getLogger().info(" - " + entry.getKey() + ": " + entry.getValue() + " Entities");
        }

        double tps = Bukkit.getTPS()[0];
        plugin.getLogger().info("Aktuelle TPS: " + String.format("%.2f", tps));
    }
}
