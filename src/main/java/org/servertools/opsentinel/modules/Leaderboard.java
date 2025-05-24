
package org.servertools.opsentinel.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.*;

public class Leaderboard {

    public void showLeaderboard() {
        Map<String, Integer> playtimes = new HashMap<>();
        Map<String, Integer> deaths = new HashMap<>();
        Map<String, Double> kdEfficiency = new HashMap<>();

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if (!p.hasPlayedBefore()) continue;
            String name = p.getName();
            int minutes = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / (20 * 60);
            int totalDeaths = p.getStatistic(Statistic.DEATHS);

            playtimes.put(name, minutes);
            deaths.put(name, totalDeaths);
            kdEfficiency.put(name, minutes == 0 ? 0.0 : (double) totalDeaths / minutes);
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Top 3 Spielzeit:");
        showTop(playtimes, Comparator.comparingInt(v -> -v), "Minuten");

        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Top 3 Tode:");
        showTop(deaths, Comparator.comparingInt(v -> -v), "Tode");

        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Top 3 ineffizient (Tode pro Minute):");
        showTop(kdEfficiency, Comparator.comparingDouble(v -> -v), "Tode/min");
    }

    private <T extends Number> void showTop(Map<String, T> map, Comparator<T> comparator, String suffix) {
        map.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(comparator))
            .limit(3)
            .forEach(e -> Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "- " + e.getKey() + ": " + e.getValue() + " " + suffix));
    }
}
