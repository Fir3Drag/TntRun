package com.fir3drag.tntrun.arena;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerMaps {
    private final TntRun plugin;

    public PlayerMaps(TntRun plugin) {
        this.plugin = plugin;
    }

    public void addToPlaying(String arenaName, Player player){
        List<Player> playerList = this.plugin.playingMap.get(arenaName);

        if (!playerList.contains(player)){  // prevent duplicates in the list
            playerList.add(player);
        }
        this.plugin.playingMap.replace(arenaName, playerList);  // allows the player to edit the world
    }

    public void addToSpectating(String arenaName, Player player){
        List<Player> spectatingList = this.plugin.spectatingMap.get(arenaName);

        if (!spectatingList.contains(player)){  // prevent duplicates in the list
            spectatingList.add(player);
        }
        this.plugin.spectatingMap.replace(arenaName, spectatingList);  // allows the player to edit the world
        this.plugin.spectator.setSpectator(arenaName, player);
    }

    public void addToEditing(String arenaName, Player player){
        List<Player> editingList = this.plugin.editingMap.get(arenaName);

        if (!editingList.contains(player)){  // prevent duplicates in the list
            editingList.add(player);
            player.setGameMode(GameMode.CREATIVE);
        }
        this.plugin.editingMap.replace(arenaName, editingList);  // allows the player to edit the world
    }

    public void removeFromPlaying(String arenaName, Player player){
        // remove the player from current playingList list
        List<Player> playingList = this.plugin.playingMap.get(arenaName);
        playingList.remove(player);
        this.plugin.playingMap.replace(arenaName, playingList);
    }

    public void removeFromSpectating(String arenaName, Player player){
        // remove the player from current spectating list
        List<Player> spectatingList = this.plugin.spectatingMap.get(arenaName);
        spectatingList.remove(player);
        this.plugin.spectatingMap.replace(arenaName, spectatingList);
    }

    public void removeFromEditing(String arenaName, Player player){
        // remove the player from the current world editing list
        List<Player> editingList = this.plugin.editingMap.get(arenaName);

        if (editingList.contains(player)){
            player.setGameMode(GameMode.SURVIVAL);
        }
        editingList.remove(player);
        this.plugin.editingMap.replace(arenaName, editingList);
    }

    public void removeAll(String arenaName, Player player){
        removeFromPlaying(arenaName, player);
        removeFromSpectating(arenaName, player);
        removeFromEditing(arenaName, player);
    }
}
