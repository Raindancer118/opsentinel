
package org.servertools.opsentinel.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashSet;
import java.util.Set;

public class FlyByManager {
    private final Set<String> activeFly = new HashSet<>();

    public void toggleFly(Player target, Player sender) {
        String name = target.getName();
        if (activeFly.contains(name)) {
            target.setAllowFlight(false);
            target.setFlying(false);
            activeFly.remove(name);
            sender.sendMessage(ChatColor.GREEN + "FlyBy für " + name + " deaktiviert.");
        } else {
            target.setAllowFlight(true);
            target.setFlying(true);
            activeFly.add(name);
            sender.sendMessage(ChatColor.YELLOW + "FlyBy für " + name + " aktiviert. Wird beim Landen deaktiviert.");
            monitorLanding(target);
        }
    }

    private void monitorLanding(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                if (!player.isFlying() && player.getVelocity().getY() == 0) {
                    player.setAllowFlight(false);
                    activeFly.remove(player.getName());
                    cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Opsentinel"), 20L, 20L);
    }
}
