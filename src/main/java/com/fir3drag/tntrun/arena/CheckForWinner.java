package com.fir3drag.tntrun.arena;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.tasks.GameEndTask;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CheckForWinner {
    private final TntRun plugin;

    public CheckForWinner(TntRun plugin) {
        this.plugin = plugin;
    }

    public void check(World arena, Player player){  // checks if the game is over
        String arenaName = arena.getName();

        if (this.plugin.gameStatusMap.get(arenaName).equals("playing")) {
            if (this.plugin.playingMap.get(arenaName).size() > 1) {
                for (Player p : arena.getPlayers()) { // if not msgs all the players how many are left
                    p.sendMessage("There are " + this.plugin.playingMap.get(arenaName).size() + " left");
                }
            } else {
                for (Player p : arena.getPlayers()) { // if game is over msgs players the winner
                    p.sendMessage(ChatColor.YELLOW + this.plugin.playingMap.get(arenaName).get(0).getDisplayName() + " has won!");
                }
                this.plugin.changePlayerMaps.removePlayerFromPlaying(arenaName, this.plugin.playingMap.get(arenaName).get(0)); // remove the player from playing list
                new GameEndTask(plugin, player.getWorld());
            }
        }
    }
}
