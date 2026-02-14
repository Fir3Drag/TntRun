package com.fir3drag.tntrun.arena;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class Countdown {
    private final TntRun plugin;

    public Countdown(TntRun plugin) {
        this.plugin = plugin;
    }

    // modify is used to change the player count if this code runs before or after the player joins the game
    public void checkForStart(World arena, Integer modify){
        String arenaName = arena.getName();
        List<Player> playingList = this.plugin.playingMap.get(arenaName);

        int halfCountdown = this.plugin.data.getTntRunConfig().getInt("halfCountdown");
        int quarterCountdown = this.plugin.data.getTntRunConfig().getInt("quarterCountdown");
        int requiredPlayersToStartCountdown = this.plugin.data.getTntRunConfig().getInt("requiredPlayersToStartCountdown");
        int requiredPlayersToStartHalfCountdown = this.plugin.data.getTntRunConfig().getInt("requiredPlayersToStartHalfCountdown");
        int requiredPlayersToStartQuarterCountdown = this.plugin.data.getTntRunConfig().getInt("requiredPlayersToStartQuarterCountdown");

        // checks if the count needs to be started
        if (playingList.size() >= requiredPlayersToStartQuarterCountdown + modify && this.plugin.gameStatusMap.get(arenaName).equals("stopped") && !this.plugin.countdownMap.get(arenaName).isCounting()) {
            this.plugin.countdownMap.get(arenaName).startCountdown();
            this.plugin.countdownMap.get(arenaName).modifyCountdown(quarterCountdown);
        }
        else if (playingList.size() >= requiredPlayersToStartHalfCountdown + modify && this.plugin.gameStatusMap.get(arenaName).equals("stopped") && !this.plugin.countdownMap.get(arenaName).isCounting()) {
            this.plugin.countdownMap.get(arenaName).startCountdown();
            this.plugin.countdownMap.get(arenaName).modifyCountdown(halfCountdown);
        }
        else if (playingList.size() >= requiredPlayersToStartCountdown + modify && this.plugin.gameStatusMap.get(arenaName).equals("stopped") && !this.plugin.countdownMap.get(arenaName).isCounting()) {
            this.plugin.countdownMap.get(arenaName).startCountdown();
        }
    }

    public void checkForCancel(World arena){
        String arenaName = arena.getName();
        List<Player> playingList = this.plugin.playingMap.get(arenaName);

        int requiredPlayersToStartCountdown = this.plugin.data.getTntRunConfig().getInt("requiredPlayersToStartCountdown");

        if (playingList.size() <= requiredPlayersToStartCountdown && this.plugin.gameStatusMap.get(arenaName).equals("starting") && this.plugin.countdownMap.get(arenaName).isCounting()) {
            this.plugin.countdownMap.get(arenaName).cancelCountdown();
        }
    }
}
