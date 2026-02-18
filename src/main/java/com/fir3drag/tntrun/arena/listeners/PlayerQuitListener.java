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
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        Player player = event.getPlayer();
        World arena = event.getPlayer().getWorld();
        String arenaName = arena.getName();

        if (arenas.contains(arenaName)){  // checks if its an arena and handles players leaving during start phase
            this.plugin.spectator.showAllPlayersYouAndYouAllPlayers(arenaName, player);
            this.plugin.playerMaps.removeAll(arenaName, player);
            this.plugin.countdown.checkForCancel(arena);
            this.plugin.winner.checkForWinner(arena, player);
        }
    }
}
