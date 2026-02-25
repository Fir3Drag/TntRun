package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final TntRun plugin;

    public PlayerQuitListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        this.plugin.gameController.handleQuit(event.getPlayer());
        this.plugin.lobbyController.handleQuit(event.getPlayer());
        this.plugin.scoreboardController.handleQuit(event.getPlayer());
    }
}
