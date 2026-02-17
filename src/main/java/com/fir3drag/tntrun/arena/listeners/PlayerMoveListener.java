package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PlayerMoveListener implements Listener {
    private final TntRun plugin;

    public PlayerMoveListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        // variables
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        long deathYLevel = this.plugin.data.getTntRunConfig().getInt("playerDeathYLevel");
        long voidYLevel = this.plugin.data.getTntRunConfig().getInt("voidYLevel");
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();
        World arena = player.getWorld();

        // handles player falling = lose game
        if (arenas.contains(arenaName)){ // checks your in an arena
            if (this.plugin.playingMap.get(arenaName).contains(player) && this.plugin.gameStatusMap.get(arenaName).equals("playing") &&
                    player.getLocation().getBlockY() < deathYLevel){ // check if you are playing and have fallen off
                for (Player p: this.plugin.playingMap.get(arenaName)){ // msgs all players in the world
                    p.sendMessage(player.getDisplayName() + " has died.");
                }
                this.plugin.changePlayerMaps.removePlayerFromPlaying(arenaName, player);
                this.plugin.changePlayerMaps.addPlayerToSpectating(arenaName, player);
                this.plugin.checkForWinner.check(arena, player);
            }
        }

        // handles players in void to tp them to world spawn
        if (arenas.contains(arenaName)){  // player in arena
            if (this.plugin.spectatingMap.get(arenaName).contains(player) || this.plugin.playingMap.get(arenaName).contains(player)
                    && player.getLocation().getBlockY() < voidYLevel){  // checks they are in the void
                player.teleport(player.getWorld().getSpawnLocation()); // teleports them to world spawn
            }
        }

        // handle players playing to remove blocks below them
        this.plugin.blockRemoverTask.move(event.getPlayer());
    }
}
