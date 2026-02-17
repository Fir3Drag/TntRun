package com.fir3drag.tntrun.arena.tasks;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

public class GameEndTask {
    public GameEndTask(TntRun plugin, World arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // show all players to you and you to them
                for (Player p : plugin.playingMap.get(arena.getName())) {
                    plugin.customSpectator.showAllPlayers(arena.getName(), p);
                }
                for (Player p : plugin.spectatingMap.get(arena.getName())) {
                    plugin.customSpectator.showAllPlayers(arena.getName(), p);
                }

                // send the players back to the lobby and clear the maps holding player info
                for (Player p : new ArrayList<>(plugin.playingMap.get(arena.getName()))) {
                    plugin.changePlayerMaps.removePlayerFromPlaying(arena.getName(), p);
                    p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                for (Player p : new ArrayList<>(plugin.spectatingMap.get(arena.getName()))) {
                    plugin.changePlayerMaps.removePlayerFromSpectating(arena.getName(), p);
                    p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }

                // gets the rollback map and rolls the blocks back, then clears the map
                Map<Block, Material> rollbackMap = plugin.rollbackMap.get(arena.getName());

                for (Block b: rollbackMap.keySet()){
                    b.setType(rollbackMap.get(b));
                }
                rollbackMap.clear();
                plugin.rollbackMap.replace(arena.getName(), rollbackMap);

                plugin.gameStatusMap.replace(arena.getName(), "stopped");
            }
        }.runTaskLater(plugin, 20 * 5L);
    }
}
