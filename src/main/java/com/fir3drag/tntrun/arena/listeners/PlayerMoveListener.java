package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private final TntRun plugin;

    public PlayerMoveListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        this.plugin.gameController.handleEnteringVoid(event.getPlayer());  // tps players out of the void
        this.plugin.gameController.handlePlayerFall(event.getPlayer());  // handles player elimination
        this.plugin.gameController.handleTntRemover(event.getPlayer());  // handle players playing to remove blocks below them
    }
}
