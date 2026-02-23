package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class PlayerMapsController {
    private final TntRun plugin;

    public PlayerMapsController(TntRun plugin) {
        this.plugin = plugin;
    }

    public void addToPlaying(String arenaName, Player player){
        List<Player> playerList = this.plugin.playingMap.get(arenaName);

        if (!playerList.contains(player)){  // prevent duplicates in the list
            playerList.add(player);
            this.plugin.playingMap.replace(arenaName, playerList);  // allows the player to edit the world
            this.plugin.scoreboardMap.get(player).refresh(arenaName);
        }
    }

    public void addToSpectating(String arenaName, Player player){
        List<Player> spectatingList = this.plugin.spectatingMap.get(arenaName);

        if (!spectatingList.contains(player)){  // prevent duplicates in the list
            spectatingList.add(player);
            this.plugin.spectatingMap.replace(arenaName, spectatingList);  // allows the player to edit the world
            this.plugin.spectatorController.setSpectator(arenaName, player); // needs to be after the replace as it uses the list
            this.plugin.scoreboardMap.get(player).refresh(arenaName);
        }
    }

    public void addToEditing(String arenaName, Player player){
        List<Player> editingList = this.plugin.editingMap.get(arenaName);

        if (!editingList.contains(player)){  // prevent duplicates in the list
            editingList.add(player);
            this.plugin.editingMap.replace(arenaName, editingList);  // allows the player to edit the world
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    // remove the player from current playingList list and trigger required functions
    public void removeFromPlaying(String arenaName, Player player){
        List<Player> playingList = this.plugin.playingMap.get(arenaName);
        playingList.remove(player);
        this.plugin.playingMap.replace(arenaName, playingList);
        this.plugin.countdownController.checkForCancel(arenaName);  // handles players leaving during start phase
        this.plugin.winController.checkForWinner(arenaName, player);   // handles players leaving during game
        this.plugin.scoreboardMap.get(player).refresh(arenaName); // removes scoreboards when player leaves the game
    }

    // remove the player from current spectating list and trigger required functions
    public void removeFromSpectating(String arenaName, Player player){
        List<Player> spectatingList = this.plugin.spectatingMap.get(arenaName);
        spectatingList.remove(player);
        this.plugin.spectatingMap.replace(arenaName, spectatingList);
        this.plugin.spectatorController.showAllPlayersYouAndYouAllPlayers(arenaName, player);
        this.plugin.scoreboardMap.get(player).refresh(arenaName); // removes scoreboards when player leaves the game
    }

    // remove the player from the current world editing list and trigger required functions
    public void removeFromEditing(String arenaName, Player player){
        List<Player> editingList = this.plugin.editingMap.get(arenaName);
        player.setGameMode(GameMode.SURVIVAL);
        editingList.remove(player);
        this.plugin.editingMap.replace(arenaName, editingList);
    }

    public void removeAll(String arenaName, Player player){
        if (this.plugin.playingMap.get(arenaName).contains(player)){
            removeFromPlaying(arenaName, player);
        }

        if (this.plugin.spectatingMap.get(arenaName).contains(player)){
            removeFromSpectating(arenaName, player);
        }

        if (this.plugin.editingMap.get(arenaName).contains(player)){
            removeFromEditing(arenaName, player);
        }
    }
}
