package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {
    private final TntRun plugin;

    public FoodLevelChangeListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event){
        this.plugin.worldController.handlePlayerHunger(event);
    }
}
