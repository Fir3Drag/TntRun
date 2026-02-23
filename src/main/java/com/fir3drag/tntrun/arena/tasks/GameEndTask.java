package com.fir3drag.tntrun.arena.tasks;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

public class GameEndTask {
    public GameEndTask(TntRun plugin, World arena) {
        String arenaName = arena.getName();

        new BukkitRunnable() {
            @Override
            public void run() {
                // send the players back to the lobby and clear the maps holding player info
                for (Player p : new ArrayList<>(plugin.playingMap.get(arenaName))) {
                    plugin.playerMapsController.removeFromPlaying(arenaName, p);
                    plugin.lobbyController.tp(p); // send player to lobby
                }
                for (Player p : new ArrayList<>(plugin.spectatingMap.get(arenaName))) {
                    plugin.playerMapsController.removeFromSpectating(arenaName, p);
                    plugin.lobbyController.tp(p); // send spectator to the lobby
                }

                // gets the rollback map and rolls the blocks back, then clears the map
                Map<Block, Material> rollbackMap = plugin.rollbackMap.get(arenaName);

                for (Block b: rollbackMap.keySet()){
                    b.setType(rollbackMap.get(b));
                }
                rollbackMap.clear();
                plugin.rollbackMap.replace(arenaName, rollbackMap);

                plugin.gameStatusMap.replace(arenaName, "stopped");
            }
        }.runTaskLater(plugin, 20 * 5L);
    }
}
