package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final TntRun plugin;

    public PlayerJoinListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        this.plugin.defaultValues.handleJoin(event.getPlayer());
        this.plugin.worldController.handlePlayerJoin(event.getPlayer());
        this.plugin.lobbyController.handleJoin(event);
        this.plugin.gameController.handleRejoin(event.getPlayer());
        this.plugin.scoreboardController.handleJoin(event.getPlayer());
    }
}
