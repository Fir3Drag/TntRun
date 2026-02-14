package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.List;

public class WorldModifyListener implements Listener {
    private final TntRun plugin;

    public WorldModifyListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            if (this.plugin.editingMap.get(arenaName).contains(player)){  // if they are in the list prevents you getting to the cancel event
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            if (this.plugin.editingMap.get(arenaName).contains(player)){   // if they are in the list prevents you getting to the cancel event
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        String arenaName = event.getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        String arenaName = event.getEntity().getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            event.setCancelled(true);
        }
    }
}
