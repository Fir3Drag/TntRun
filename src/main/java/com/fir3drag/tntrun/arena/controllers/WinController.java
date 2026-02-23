package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.tasks.GameEndTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WinController {
    private final TntRun plugin;

    public WinController(TntRun plugin) {
        this.plugin = plugin;
    }

    public void checkForWinner(String arenaName, Player player){  // checks if the game is over
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
                Player winner = this.plugin.playingMap.get(arenaName).get(0);  // gets the winner
                this.plugin.playingCountUpMap.get(arenaName).cancelCountdown();  // stop the timer

                for (Player p : this.plugin.playingMap.get(arenaName)) { // msgs players
                    p.sendMessage(ChatColor.YELLOW + winner.getDisplayName() + " has won!");
                    this.plugin.spectatorController.showAllPlayersYouAndYouAllPlayers(arenaName, p);
                }
                for (Player p : this.plugin.spectatingMap.get(arenaName)) { // msgs spectators
                    p.sendMessage(ChatColor.YELLOW + winner.getDisplayName() + " has won!");
                    this.plugin.spectatorController.showAllPlayersYouAndYouAllPlayers(arenaName, p);
                }
                int playerWinCount = this.plugin.data.getDataConfig().getInt(winner.getUniqueId().toString());
                this.plugin.data.getDataConfig().set(winner.getUniqueId().toString(), ++playerWinCount);
                this.plugin.data.saveConfig();

                plugin.gameStatusMap.replace(arenaName, "restarting"); // placed here to stop death msg if game ends and last player is in the void
                new GameEndTask(plugin, player.getWorld());
            }
        }
    }
}
