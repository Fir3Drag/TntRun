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
                for (Player p : this.plugin.playingMap.get(arenaName)) { // msgs players
                    p.sendMessage(ChatColor.YELLOW  + "There are "  + this.plugin.playingMap.get(arenaName).size() + " left");
                }
                for (Player p : this.plugin.spectatingMap.get(arenaName)) { // msgs spectators
                    p.sendMessage(ChatColor.YELLOW  + "There are " + this.plugin.playingMap.get(arenaName).size() + " left");
                }
            }
            else {
                for (Player p : this.plugin.playingMap.get(arenaName)) { // msgs players
                    p.sendMessage(ChatColor.YELLOW + this.plugin.playingMap.get(arenaName).get(0).getDisplayName() + " has won!");
                }
                for (Player p : this.plugin.spectatingMap.get(arenaName)) { // msgs spectators
                    p.sendMessage(ChatColor.YELLOW + this.plugin.playingMap.get(arenaName).get(0).getDisplayName() + " has won!");
                }
                plugin.gameStatusMap.replace(arena.getName(), "restarting"); // placed here to stop death msg if game ends and last player is in the void
                new GameEndTask(plugin, player.getWorld());
            }
        }
    }
}
