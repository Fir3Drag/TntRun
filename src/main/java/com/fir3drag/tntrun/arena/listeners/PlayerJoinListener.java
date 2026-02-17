package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {
    private final TntRun plugin;

    public PlayerJoinListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        int maxPlayers = this.plugin.data.getTntRunConfig().getInt("maxPlayers");

        Player player = event.getPlayer();
        World arena = event.getPlayer().getWorld();
        String arenaName = arena.getName();

        if (arenas.contains(arenaName)){  // checks if its an arena
            if (this.plugin.playingMap.get(arenaName).size() > maxPlayers && this.plugin.gameStatusMap.get(arenaName).equals("starting")){
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                player.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' you were in is full, join back as a spectator after it starts.");
                return;
            }

            // update map values
            if (this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("stopped") ||
                    this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("starting")){
                this.plugin.changePlayerMaps.addPlayerToPlaying(arenaName, player);
            }

            if (this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("playing")){
                this.plugin.changePlayerMaps.addPlayerToSpectating(arenaName, player);
            }

            // handles players joining during start phase
            this.plugin.countdown.checkForStart(player.getWorld(), -1);
        }
    }
}
