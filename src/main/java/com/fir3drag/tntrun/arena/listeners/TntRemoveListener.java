package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TntRemoveListener implements Listener {
    private final TntRun plugin;

    public TntRemoveListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        this.plugin.tntRunBlockRemover.move(event.getPlayer());
    }
}
