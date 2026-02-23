package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.controllers.ScoreboardController;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.List;

public class PlayerJoinListener implements Listener {
    private final TntRun plugin;

    public PlayerJoinListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String playerWinCount = this.plugin.data.getDataConfig().getString(player.getUniqueId().toString());

        // check that you have a stored win count
        if (playerWinCount == null){
            this.plugin.data.getDataConfig().set(player.getUniqueId().toString(), 0);
            this.plugin.data.saveConfig();
        }

        // creates a scoreboard controller for each player that joins
        if (!this.plugin.scoreboardMap.containsKey(player)){
            this.plugin.scoreboardMap.put(player, new ScoreboardController(this.plugin, player));
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        int maxPlayers = this.plugin.data.getTntRunConfig().getInt("maxPlayers");
        World arena = event.getPlayer().getWorld();
        String arenaName = arena.getName();

        // handles players reconnecting into an arena
        if (arenas.contains(arenaName)){  // checks if its an arena
            // if the arena is full your set as a spectator on rejoin
            if (this.plugin.playingMap.get(arenaName).size() > maxPlayers && this.plugin.gameStatusMap.get(arenaName).equals("starting")){
                this.plugin.playerMapsController.addToSpectating(arenaName, player);
                player.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' you were in is full, you're now a spectator.");
                return;
            }

            // if the game state is starting or stopped add you as a player
            if (this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("stopped") ||
                    this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("starting")){
                this.plugin.playerMapsController.addToPlaying(arenaName, player);
            }

            // if the game state is now playing add you as a spectator
            if (this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("playing")){
                this.plugin.playerMapsController.addToSpectating(arenaName, player);
            }

            // handles players joining during start phase
            this.plugin.countdownController.checkForStart(player.getWorld().getName(), -1);
        }
    }
}
