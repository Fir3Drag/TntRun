package com.fir3drag.tntrun.arena.tasks;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class GameEndTask {
    public GameEndTask(TntRun plugin, World arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : arena.getPlayers()) { // send the players back to the lobby
                    plugin.changePlayerMaps.removePlayerFromPlaying(arena.getName(), p);  // run before tp to show spec to the right players
                    p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                plugin.gameStatusMap.replace(arena.getName(), "restarting");

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
