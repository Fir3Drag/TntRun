package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class VoidListener implements Listener {
    private final TntRun plugin;

    public VoidListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVoidTouch(PlayerMoveEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        int spectatorVoidYLevel = this.plugin.data.getTntRunConfig().getInt("spectatorVoidYLevel");
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){  // player in arena
            if (this.plugin.spectatingMap.get(arenaName).contains(player) && player.getLocation().getBlockY() < spectatorVoidYLevel){  // checks they are in the void
                player.teleport(player.getWorld().getSpawnLocation()); // teleports them to world spawn
            }
        }
    }
}
