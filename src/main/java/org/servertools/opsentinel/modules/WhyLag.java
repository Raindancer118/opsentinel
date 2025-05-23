
package org.servertools.opsentinel.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class WhyLag {

    private final Plugin plugin;

    public WhyLag(Plugin plugin) {
        this.plugin = plugin;
    }

    public void analyze(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "[Lag-Analyse] Mögliche Ursachen:");

        double tps = Bukkit.getTPS()[0];
        sender.sendMessage(ChatColor.GRAY + "TPS aktuell: " + ChatColor.AQUA + String.format("%.2f", tps));

        for (World world : Bukkit.getWorlds()) {
            int entityCount = world.getEntities().size();
            int tileEntityCount = world.getChunkCount(); // Approximation
            sender.sendMessage(ChatColor.GRAY + "Welt: " + world.getName());
            sender.sendMessage("  " + ChatColor.GOLD + "- Entities: " + ChatColor.WHITE + entityCount);
            sender.sendMessage("  " + ChatColor.GOLD + "- Chunks geladen: " + ChatColor.WHITE + world.getLoadedChunks().length);
        }

        sender.sendMessage(ChatColor.GRAY + "Weitere Analyse mit Spark empfohlen bei starken TPS-Einbrüchen.");
    }
}
