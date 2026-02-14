package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class FallListener implements Listener {
    private final TntRun plugin;
    private List<Block> rollback;

    public FallListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event){
        // variables
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        int deathYLevel = this.plugin.data.getTntRunConfig().getInt("playerDeathYLevel");
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();
        World arena = player.getWorld();

        if (arenas.contains(arenaName)){ // checks your in an arena
            if (this.plugin.playingMap.get(arenaName).contains(player) && player.getLocation().getBlockY() < deathYLevel){ // check if you are playing and have fallen off
                for (Player p: arena.getPlayers()){ // msgs all players in the world
                    p.sendMessage(player.getDisplayName() + " has died");
                }
                this.plugin.changePlayerMaps.removePlayerFromPlaying(arenaName, player);
                this.plugin.changePlayerMaps.addPlayerToSpectating(arenaName, player);
                this.plugin.checkForWinner.check(arena, player);
            }
        }
    }
}


