
package org.servertools.opsentinel.modules;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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
        int playersOnline = Bukkit.getOnlinePlayers().size();

        Map<String, Integer> entityPerWorld = new HashMap<>();
        String worstWorld = "";
        int maxEntities = 0;

        for (World world : Bukkit.getWorlds()) {
            int entities = world.getEntities().size();
            int chunks = world.getLoadedChunks().length;

            totalEntities += entities;
            loadedChunks += chunks;
            entityPerWorld.put(world.getName(), entities);

            if (entities > maxEntities) {
                maxEntities = entities;
                worstWorld = world.getName();
            }
        }

        double tps = Bukkit.getTPS()[0];

        plugin.getLogger().info("Spieler online: " + playersOnline);
        plugin.getLogger().info("Geladene Chunks: " + loadedChunks);
        plugin.getLogger().info("Gesamte Entities: " + totalEntities);
        plugin.getLogger().info("TPS: " + String.format("%.2f", tps));

        for (Map.Entry<String, Integer> entry : entityPerWorld.entrySet()) {
            plugin.getLogger().info(" - " + entry.getKey() + ": " + entry.getValue() + " Entities");
        }

        plugin.getLogger().info("===== MÖGLICHE URSACHEN =====");

        if (tps >= 19.5) {
            plugin.getLogger().info("✅ Keine auffälligen TPS-Probleme.");
        } else {
            if (totalEntities > 1000) {
                plugin.getLogger().warning("⚠ Sehr viele Entities insgesamt – besonders in Welt: " + worstWorld);
            }
            if (loadedChunks > 1000) {
                plugin.getLogger().warning("⚠ Sehr viele geladene Chunks – möglicherweise durch erkundende Spieler/Farmen.");
            }
            if (playersOnline > 10) {
                plugin.getLogger().info("ℹ Viele Spieler online. Das kann die Performance beeinflussen.");
            }
            if (tps < 15.0) {
                plugin.getLogger().warning("❗ Kritisch niedrige TPS! Unbedingt Lag-Quellen untersuchen.");
            } else {
                plugin.getLogger().info("⚠ TPS unter 19.5 – leichte Verzögerungen möglich.");
            }
        }

        plugin.getLogger().info("Analyse abgeschlossen.");
    }
}
