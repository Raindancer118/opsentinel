package org.servertools.opsentinel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class OpsentinelPlugin extends JavaPlugin {

    private File configFile;
    private final Map<String, String> config = new HashMap<>();

    @Override
    public void onEnable() {
        loadConfigFile();
        getLogger().info("Opsentinel geladen.");
    }

    private void loadConfigFile() {
        configFile = new File(getDataFolder(), "config.txt");
        if (!configFile.exists()) {
            saveDefaultConfigFile();
        }
        try {
            config.clear();
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    config.put(parts[0].trim(), parts[1].trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            getLogger().warning("Fehler beim Lesen der Konfiguration: " + e.getMessage());
        }
    }

    private void saveDefaultConfigFile() {
        getDataFolder().mkdirs();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("# Opsentinel Konfigurationsdatei");
            writer.write("ping_warn_ms=500");
            writer.write("drop_warn_amount=100");
            writer.write("afk_timeout_minutes=10");
        } catch (IOException e) {
            getLogger().warning("Fehler beim Schreiben der Standardkonfiguration: " + e.getMessage());
        }
    }

    private int getConfigInt(String key, int def) {
        return config.containsKey(key) ? Integer.parseInt(config.get(key)) : def;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Nur Admins können diesen Befehl verwenden.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/ops pingstats - Zeigt aktuelle Spielerpings");
            return true;
        }

        if (args[0].equalsIgnoreCase("pingstats")) {
            int warnThreshold = getConfigInt("ping_warn_ms", 500);
            sender.sendMessage(ChatColor.GOLD + "Aktuelle Spieler-Pings:");
            for (Player player : Bukkit.getOnlinePlayers()) {
                int ping = player.getPing();
                String color = ping > warnThreshold ? "§c" : "§a";
                sender.sendMessage(color + "- " + player.getName() + ": " + ping + " ms");
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unbekannter Subbefehl. Nutze /ops für Hilfe.");
        return true;
    }
}
