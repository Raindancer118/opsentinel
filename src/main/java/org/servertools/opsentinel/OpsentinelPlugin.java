
package org.servertools.opsentinel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.servertools.opsentinel.modules.FlyByManager;
import org.servertools.opsentinel.modules.LagAnalyzer;
import org.servertools.opsentinel.modules.PlayerStats;

import java.io.*;
import java.util.*;

public class OpsentinelPlugin extends JavaPlugin {

    private File configFile;
    private final Map<String, String> config = new HashMap<>();

    private FlyByManager flyByManager;
    private LagAnalyzer lagAnalyzer;
    private PlayerStats playerStats;
    private Leaderboard leaderboard;

    @Override
    public void onEnable() {
        loadConfigFile();
        flyByManager = new FlyByManager();
        lagAnalyzer = new LagAnalyzer(this);
        playerStats = new PlayerStats();
        leaderboard = new Leaderboard();
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
            writer.write("# Opsentinel Konfigurationsdatei\n");
            writer.write("ping_warn_ms=500\n");
            writer.write("drop_warn_amount=100\n");
            writer.write("afk_timeout_minutes=10\n");
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

        String cmd = label.toLowerCase();
        if (cmd.equals("whylag")) {
            lagAnalyzer.runAnalysis();
            return true;
        }

        if (!cmd.equals("ops")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/ops pingstats - Zeigt aktuelle Spielerpings");
            sender.sendMessage(ChatColor.YELLOW + "/ops flyby [Spieler]");
            sender.sendMessage(ChatColor.YELLOW + "/ops playerstats <Spieler>");
            sender.sendMessage(ChatColor.YELLOW + "/ops whylag");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pingstats" -> {
                int warnThreshold = getConfigInt("ping_warn_ms", 500);
                sender.sendMessage(ChatColor.GOLD + "Aktuelle Spieler-Pings:");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int ping = player.getPing();
                    String color = ping > warnThreshold ? "§c" : "§a";
                    sender.sendMessage(color + "- " + player.getName() + ": " + ping + " ms");
                }
                return true;
            }
            case "flyby" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Verwendung: /ops flyby <Spieler>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden.");
                    return true;
                }
                flyByManager.toggleFly(target, sender instanceof Player ? (Player) sender : target);
                return true;
            }
            case "playerstats" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Verwendung: /ops playerstats <Spieler>");
                    return true;
                }
                playerStats.showStats(sender, args[1]);
                return true;
            }
            case "leaderboard" -> {
                leaderboard.showLeaderboard();
                return true;
            }
            case "whylag" -> {
                lagAnalyzer.runAnalysis();
                return true;
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Unbekannter Subbefehl. Nutze /ops für Hilfe.");
                return true;
            }
        }
    }
}
