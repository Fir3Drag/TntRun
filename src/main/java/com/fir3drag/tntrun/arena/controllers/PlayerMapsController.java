package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerMapsController {
    private final TntRun plugin;

    public PlayerMapsController(TntRun plugin) {
        this.plugin = plugin;
    }

    public void addToPlaying(String arenaName, Player player){
        List<Player> playerList = this.plugin.playingMap.get(arenaName);
        boolean forcePlayerGameMode = this.plugin.defaultValues.getForcePlayerGameMode();
        boolean setFullHealth = this.plugin.defaultValues.getSetFullHealth();
        boolean setFullHunger = this.plugin.defaultValues.getSetFullHunger();

        if (!playerList.contains(player)){  // prevent duplicates in the list

            // modify player
            if (forcePlayerGameMode) player.setGameMode(GameMode.SURVIVAL);
            if (setFullHealth) player.setHealth(20);
            if (setFullHunger) player.setFoodLevel(20);

            playerList.add(player);
            this.plugin.playingMap.replace(arenaName, playerList);  // allows the player to edit the world
            this.plugin.lobbyList.remove(player);
            this.plugin.lobbyEditList.remove(player);
            this.plugin.countdownController.checkForStart(arenaName);  // handles the countdown times depending on player size
            this.plugin.scoreboardController.refresh(arenaName, player);
            this.plugin.gameController.createSpectatingCompassInventory(arenaName);
            this.plugin.lobbyController.createJoiningArenaInventory();
            this.plugin.gameController.setStartingInventory(player);

            for (Player p: this.plugin.playingMap.get(arenaName)){
                p.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " has joined the arena. " + this.plugin.playingMap.get(arenaName).size() +
                        "/" + this.plugin.defaultValues.getMaxPlayers());
            }
            for (Player p: this.plugin.spectatingMap.get(arenaName)){
                p.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " has joined the arena. " + this.plugin.playingMap.get(arenaName).size() +
                        "/" + this.plugin.defaultValues.getMaxPlayers());
            }
        }
    }

    public void addToSpectating(String arenaName, Player player){
        List<Player> spectatingList = this.plugin.spectatingMap.get(arenaName);

        if (!spectatingList.contains(player)){  // prevent duplicates in the list
            spectatingList.add(player);
            this.plugin.spectatingMap.replace(arenaName, spectatingList);  // allows the player to edit the world
            this.plugin.lobbyList.remove(player);
            this.plugin.lobbyEditList.remove(player);
            this.plugin.gameController.setSpectator(arenaName, player); // needs to be after the replace as it uses the list
            this.plugin.scoreboardController.refresh(arenaName, player);
        }
    }

    public void addToEditing(String arenaName, Player player){
        List<Player> editingList = this.plugin.editingMap.get(arenaName);

        if (!editingList.contains(player)){  // prevent duplicates in the list
            editingList.add(player);
            this.plugin.editingMap.replace(arenaName, editingList);  // allows the player to edit the world
            this.plugin.lobbyList.remove(player);
            this.plugin.lobbyEditList.remove(player);
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    // remove the player from current playingList list and trigger required functions
    public void removeFromPlaying(String arenaName, Player player){
        List<Player> playingList = this.plugin.playingMap.get(arenaName);
        playingList.remove(player);
        this.plugin.playingMap.replace(arenaName, playingList);
        this.plugin.countdownController.checkForCancel(arenaName);  // handles players leaving during start phase
        this.plugin.scoreboardController.refresh(arenaName, player); // updates scoreboards when player leaves the game
        this.plugin.gameController.createSpectatingCompassInventory(arenaName);
        this.plugin.lobbyController.createJoiningArenaInventory();
        this.plugin.gameController.checkForWinner(arenaName, player);   // handles players leaving during game

        if (this.plugin.gameStatusMap.get(arenaName).equals("stopped") || this.plugin.gameStatusMap.get(arenaName).equals("starting")){
            for (Player p: this.plugin.playingMap.get(arenaName)){
                p.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " has left the arena. " + this.plugin.playingMap.get(arenaName).size() +
                        "/" + this.plugin.defaultValues.getMaxPlayers());
            }
            for (Player p: this.plugin.spectatingMap.get(arenaName)){
                p.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " has left the arena. " + this.plugin.playingMap.get(arenaName).size() +
                        "/" + this.plugin.defaultValues.getMaxPlayers());
            }
        }
    }

    // remove the player from current spectating list and trigger required functions
    public void removeFromSpectating(String arenaName, Player player){
        List<Player> spectatingList = this.plugin.spectatingMap.get(arenaName);
        spectatingList.remove(player);
        this.plugin.spectatingMap.replace(arenaName, spectatingList);
        this.plugin.gameController.showAllPlayersYouAndYouAllPlayers(arenaName, player);
        this.plugin.scoreboardController.refresh(arenaName, player); // updates scoreboards when player leaves the game
        this.plugin.gameController.setPlayer(player);
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
