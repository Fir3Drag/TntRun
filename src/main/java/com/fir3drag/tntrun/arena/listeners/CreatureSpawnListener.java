package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

public class CreatureSpawnListener implements Listener {
    private final TntRun plugin;

    public CreatureSpawnListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        boolean disableCreatureSpawn = this.plugin.data.getTntRunConfig().getBoolean("disableCreatureSpawn");
        String arenaName = event.getEntity().getWorld().getName();

        if (arenas.contains(arenaName) && disableCreatureSpawn){  // checks its an arena
            event.setCancelled(true);
        }
    }
}
