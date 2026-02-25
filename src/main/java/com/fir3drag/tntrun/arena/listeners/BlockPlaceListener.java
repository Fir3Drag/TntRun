package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    private final TntRun plugin;

    public BlockPlaceListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        this.plugin.lobbyController.handleBlockPlace(event);
        this.plugin.gameController.handleBlockPlace(event);
    }
}

