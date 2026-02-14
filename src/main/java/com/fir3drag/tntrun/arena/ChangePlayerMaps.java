package com.fir3drag.tntrun.arena;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.entity.Player;

import java.util.List;

public class ChangePlayerMaps {
    private final TntRun plugin;

    public ChangePlayerMaps(TntRun plugin) {
        this.plugin = plugin;
    }

    public void addPlayerToPlaying(String arenaName, Player player){
        List<Player> playerList = this.plugin.playingMap.get(arenaName);

        if (!playerList.contains(player)){  // prevent duplicates in the list
            playerList.add(player);
        }
        this.plugin.playingMap.replace(arenaName, playerList);  // allows the player to edit the world
    }

    public void addPlayerToSpectating(String arenaName, Player player){
        List<Player> spectatingList = this.plugin.spectatingMap.get(arenaName);

        if (!spectatingList.contains(player)){  // prevent duplicates in the list
            spectatingList.add(player);
        }
        this.plugin.spectatingMap.replace(arenaName, spectatingList);  // allows the player to edit the world

        this.plugin.customSpectator.enableSpectator(player);
    }

    public void addPlayerToEditing(String arenaName, Player player){
        List<Player> editingList = this.plugin.editingMap.get(arenaName);

        if (!editingList.contains(player)){  // prevent duplicates in the list
            editingList.add(player);
        }
        this.plugin.editingMap.replace(arenaName, editingList);  // allows the player to edit the world
    }

    public void removePlayerFromPlaying(String arenaName, Player player){
        // remove the player from current playingList list
        List<Player> playingList = this.plugin.playingMap.get(arenaName);
        playingList.remove(player);
        this.plugin.playingMap.replace(arenaName, playingList);
    }

    public void removePlayerFromSpectating(String arenaName, Player player){
        // remove the player from current spectating list
        List<Player> spectatingList = this.plugin.spectatingMap.get(arenaName);

        if (spectatingList.contains(player)){
            this.plugin.customSpectator.disableSpectator(player);
        }
        spectatingList.remove(player);
        this.plugin.spectatingMap.replace(arenaName, spectatingList);
    }

    public void removePlayerFromEditing(String arenaName, Player player){
        // remove the player from the current world editing list
        List<Player> editingList = this.plugin.editingMap.get(arenaName);
        editingList.remove(player);
        this.plugin.editingMap.replace(arenaName, editingList);
    }

    public void removePlayerAll(String arenaName, Player player){
        removePlayerFromPlaying(arenaName, player);
        removePlayerFromSpectating(arenaName, player);
        removePlayerFromEditing(arenaName, player);
    }
}
