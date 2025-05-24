
package org.servertools.opsentinel.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private final Plugin plugin;
    private String remoteVersion = "unknown";
    private boolean updateAvailable = false;

    public Updater(Plugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://raw.githubusercontent.com/Raindancer118/opsentinel/main/src/main/java/org/servertools/opsentinel/version.txt");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    remoteVersion = reader.readLine().trim();
                }

                String localVersion = plugin.getDescription().getVersion();
                if (!remoteVersion.equals(localVersion)) {
                    updateAvailable = true;
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Opsentinel] Neue Version verfügbar: " + remoteVersion);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Opsentinel] Nutze /ops confirm zum Aktualisieren.");
                    startExpiryCountdown();
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Konnte Versionsdatei nicht laden: " + e.getMessage());
            }
        });
    }

    private void startExpiryCountdown() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateAvailable = false;
            plugin.getLogger().info("[Opsentinel] Update-Zeitfenster abgelaufen.");
        }, 20L * 10); // 10 Sekunden
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void performUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL fileUrl = new URL("https://github.com/Raindancer118/opsentinel/raw/main/target/opsentinel-1.0.jar");
                File currentJar = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                File backupJar = new File(currentJar.getParentFile(), "opsentinel-backup.jar");

                // Backup aktuelle Version
                try (InputStream in = new FileInputStream(currentJar);
                     FileOutputStream out = new FileOutputStream(backupJar)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

                // Überschreibe aktuelle Datei mit neuer Version
                try (InputStream in = fileUrl.openStream();
                     FileOutputStream out = new FileOutputStream(currentJar)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Opsentinel] Update erfolgreich. Plugin wird neu geladen...");
                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload confirm"));
            } catch (Exception e) {
                plugin.getLogger().warning("Fehler beim Aktualisieren des Plugins: " + e.getMessage());
            }
        });
    }
}
