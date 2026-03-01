package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.entity.Player;

import java.util.List;

public class CountdownController {
    private final TntRun plugin;

    public CountdownController(TntRun plugin) {
        this.plugin = plugin;
    }

    // modify is used to change the player count if this code runs before or after the player joins the game
    public void checkForStart(String arenaName){
        List<Player> playingList = this.plugin.playingMap.get(arenaName);

        int halfCountdown = this.plugin.defaultValues.getHalfCountdown();
        int quarterCountdown = this.plugin.defaultValues.getQuarterCountdown();
        int requiredPlayersToStartCountdown = this.plugin.defaultValues.getRequiredPlayersToStartCountdown();
        int requiredPlayersToStartHalfCountdown = this.plugin.defaultValues.getRequiredPlayersToStartHalfCountdown();
        int requiredPlayersToStartQuarterCountdown = this.plugin.defaultValues.getRequiredPlayersToStartQuarterCountdown();

        if (requiredPlayersToStartCountdown < 2){  // prevent logic errors
            requiredPlayersToStartCountdown = 2;
        }

        // checks if the count needs to be started
        if (playingList.size() == requiredPlayersToStartQuarterCountdown && this.plugin.gameStatusMap.get(arenaName).equals("starting")) {
            if (this.plugin.startingCountdownMap.get(arenaName).getCountdownTime() > quarterCountdown){
                this.plugin.startingCountdownMap.get(arenaName).modifyCountdown(quarterCountdown);
            }
        }
        else if (playingList.size() == requiredPlayersToStartHalfCountdown && this.plugin.gameStatusMap.get(arenaName).equals("starting")) {
            if (this.plugin.startingCountdownMap.get(arenaName).getCountdownTime() > halfCountdown){
                this.plugin.startingCountdownMap.get(arenaName).modifyCountdown(halfCountdown);
            }
        }
        else if (playingList.size() >= requiredPlayersToStartCountdown && this.plugin.gameStatusMap.get(arenaName).equals("stopped")) {
            this.plugin.startingCountdownMap.get(arenaName).startCountdown();
        }
    }

    public void checkForCancel(String arenaName){
        List<Player> playingList = this.plugin.playingMap.get(arenaName);

        int fullCountdown = this.plugin.defaultValues.getFullCountdown();
        int halfCountdown = this.plugin.defaultValues.getHalfCountdown();
        int requiredPlayersToStartCountdown = this.plugin.defaultValues.getRequiredPlayersToStartCountdown();
        int requiredPlayersToStartHalfCountdown = this.plugin.defaultValues.getRequiredPlayersToStartHalfCountdown();
        int requiredPlayersToStartQuarterCountdown = this.plugin.defaultValues.getRequiredPlayersToStartQuarterCountdown();

        if (requiredPlayersToStartCountdown < 2){  // prevent logic errors
            requiredPlayersToStartCountdown = 2;
        }

        if (playingList.size() <= requiredPlayersToStartCountdown-1 && this.plugin.gameStatusMap.get(arenaName).equals("starting") &&
                this.plugin.startingCountdownMap.get(arenaName).isCounting()) {
            this.plugin.startingCountdownMap.get(arenaName).cancelCountdown();
        }
        else if (playingList.size() == requiredPlayersToStartHalfCountdown-1 && this.plugin.gameStatusMap.get(arenaName).equals("starting") &&
                this.plugin.startingCountdownMap.get(arenaName).isCounting()) {
            this.plugin.startingCountdownMap.get(arenaName).modifyCountdown(fullCountdown);
        }
        else if (playingList.size() == requiredPlayersToStartQuarterCountdown-1 && this.plugin.gameStatusMap.get(arenaName).equals("starting") &&
                this.plugin.startingCountdownMap.get(arenaName).isCounting()) {
            this.plugin.startingCountdownMap.get(arenaName).modifyCountdown(halfCountdown);
        }
    }
}
