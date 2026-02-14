package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerQuitListener implements Listener {
    private final TntRun plugin;

    public PlayerQuitListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){  // removes player from map lists when they disconnect to prevent errors
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        Player player = event.getPlayer();
        World currentWorld = event.getPlayer().getWorld();
        String currentWorldName = currentWorld.getName();

        if (arenas.contains(currentWorldName)){  // checks if its an arena and handles players leaving during start phase
            this.plugin.changePlayerMaps.removePlayerAll(currentWorldName, player);
            this.plugin.countdown.checkForCancel(currentWorld);
            this.plugin.checkForWinner.check(currentWorld, player);
        }
    }
}
