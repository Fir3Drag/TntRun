package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class BlockBreakListener implements Listener {
    private final TntRun plugin;

    public BlockBreakListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        this.plugin.lobbyController.handleBlockBreak(event);
        this.plugin.gameController.handleBlockBreak(event);
    }
}
