
package org.servertools.opsentinel.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class WhoDidThis implements Listener {

    private final Map<String, Map<String, String>> blockLog = new HashMap<>();
    private final Map<String, Boolean> toggleMap = new HashMap<>();

    public boolean toggle(Player player) {
        boolean newState = !toggleMap.getOrDefault(player.getName(), false);
        toggleMap.put(player.getName(), newState);
        return newState;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        log(event.getBlock(), event.getPlayer().getName() + " placed");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        log(event.getBlock(), event.getPlayer().getName() + " broke");
    }

    private void log(Block block, String action) {
        String key = block.getWorld().getName();
        String pos = block.getX() + "," + block.getY() + "," + block.getZ();
        blockLog.computeIfAbsent(key, k -> new HashMap<>()).put(pos, action + " at " + System.currentTimeMillis());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        Player player = event.getPlayer();
        if (!toggleMap.getOrDefault(player.getName(), false)) return;

        Block block = event.getClickedBlock();
        String key = block.getWorld().getName();
        String pos = block.getX() + "," + block.getY() + "," + block.getZ();
        String log = blockLog.getOrDefault(key, new HashMap<>()).getOrDefault(pos, "Keine Daten");
        player.sendMessage(ChatColor.GRAY + "[WhoDidThis] " + ChatColor.AQUA + log);
    }
}
