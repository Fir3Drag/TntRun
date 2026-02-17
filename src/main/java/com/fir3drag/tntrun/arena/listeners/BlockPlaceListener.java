package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class BlockPlaceListener implements Listener {
    private final TntRun plugin;

    public BlockPlaceListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            if (this.plugin.editingMap.get(arenaName).contains(player)){   // if they are in the list prevents you getting to the cancel event
                return;
            }
            event.setCancelled(true);
        }
    }
}

