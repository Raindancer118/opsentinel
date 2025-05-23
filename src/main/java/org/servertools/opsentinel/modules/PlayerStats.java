
package org.servertools.opsentinel.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerStats {

    public void showStats(CommandSender sender, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Spieler '" + targetName + "' nicht gefunden oder nie online.");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "[Stats für " + target.getName() + "]");

        long firstPlayed = target.getFirstPlayed();
        long lastPlayed = target.getLastPlayed();
        long playtimeTicks = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long playtimeMin = playtimeTicks / (20 * 60);

        sender.sendMessage(ChatColor.YELLOW + "• Erste Anmeldung: " + ChatColor.WHITE + formatDate(firstPlayed));
        sender.sendMessage(ChatColor.YELLOW + "• Letzte Aktivität: " + ChatColor.WHITE + formatAgo(lastPlayed));
        sender.sendMessage(ChatColor.YELLOW + "• Onlinezeit: " + ChatColor.WHITE + formatPlaytime(playtimeMin));

        sender.sendMessage(ChatColor.YELLOW + "• Blöcke abgebaut: " + ChatColor.WHITE + target.getStatistic(Statistic.MINE_BLOCK));
        sender.sendMessage(ChatColor.YELLOW + "• Tode: " + ChatColor.WHITE + target.getStatistic(Statistic.DEATHS));
        sender.sendMessage(ChatColor.YELLOW + "• Mobs getötet: " + ChatColor.WHITE + target.getStatistic(Statistic.MOB_KILLS));
        sender.sendMessage(ChatColor.YELLOW + "• Spieler getötet: " + ChatColor.WHITE + target.getStatistic(Statistic.PLAYER_KILLS));

        int cmWalked = target.getStatistic(Statistic.WALK_ONE_CM);
        sender.sendMessage(ChatColor.YELLOW + "• Strecke gelaufen: " + ChatColor.WHITE + String.format("%.2f km", cmWalked / 100000.0));

        sender.sendMessage(ChatColor.GRAY + "Weitere Werte folgen bald.");
    }

    private String formatDate(long time) {
        if (time <= 0) return "unbekannt";
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
    }

    private String formatAgo(long time) {
        long diff = System.currentTimeMillis() - time;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        if (minutes < 1) return "gerade eben";
        if (minutes < 60) return minutes + " min";
        long hours = minutes / 60;
        return hours + " h " + (minutes % 60) + " min";
    }

    private String formatPlaytime(long min) {
        long h = min / 60;
        long m = min % 60;
        return h + " h " + m + " min";
    }
}
