package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardController {
    private final TntRun plugin;
    private final Player player;

    // placeholders to prevent null errors
    private Objective lobbyObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("placeholder", "dummy");
    private Objective startingObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("placeholder", "dummy");
    private Objective playingObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("placeholder", "dummy");

    public ScoreboardController(TntRun plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        createLobby();
        player.setScoreboard(lobbyObjective.getScoreboard());
    }

    // creates the lobby scoreboard
    public void createLobby(){
        lobbyObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(ChatColor.YELLOW + "TntRun Lobby", "dummy");
        int winCount = this.plugin.data.getDataConfig().getInt(player.getUniqueId().toString());

        Score line1 = lobbyObjective.getScore(ChatColor.YELLOW + "-----------------");
        Score line2 = lobbyObjective.getScore(ChatColor.YELLOW + "");
        Score line3 = lobbyObjective.getScore(ChatColor.YELLOW +  "wins: " + winCount);
        Score line4 = lobbyObjective.getScore(ChatColor.YELLOW + " ");
        Score line5 = lobbyObjective.getScore(ChatColor.YELLOW + "----------------- ");
        Score line6 = lobbyObjective.getScore(ChatColor.YELLOW + "yourserver.com");


        line1.setScore(6);
        line2.setScore(5);
        line3.setScore(4);
        line4.setScore(3);
        line5.setScore(2);
        line6.setScore(1);

        lobbyObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    // creates the starting scoreboard for waiting before game starts
    public void createStarting(String arenaName){
        startingObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(ChatColor.YELLOW + "TntRun Queue", "dummy");
        String duration = String.valueOf(this.plugin.startingCountdownMap.get(arenaName).getCountdownTime());
        int playerCount = this.plugin.playingMap.get(arenaName).size();
        int maxPlayers = this.plugin.data.getTntRunConfig().getInt("maxPlayers");

        if (duration.equals("-1") || !this.plugin.startingCountdownMap.get(arenaName).isCounting()){
            duration = "Waiting for players";
        }

        //TODO prevent lines being longer than 40 characters

        Score line1 = startingObjective.getScore(ChatColor.YELLOW + "-----------------");
        Score line2 = startingObjective.getScore(ChatColor.YELLOW + "Arena: " + arenaName);
        Score line3 = startingObjective.getScore(ChatColor.YELLOW + "");
        Score line4 = startingObjective.getScore(ChatColor.YELLOW +  "Duration " + duration);
        Score line5 = startingObjective.getScore(ChatColor.YELLOW + " ");
        Score line6 = startingObjective.getScore(ChatColor.YELLOW +  "Players: " + playerCount + "/" + maxPlayers);
        Score line7 = startingObjective.getScore(ChatColor.YELLOW + "  ");
        Score line8 = startingObjective.getScore(ChatColor.YELLOW + "----------------- ");
        Score line9 = startingObjective.getScore(ChatColor.YELLOW + "yourserver.com");

        line1.setScore(9);
        line2.setScore(8);
        line3.setScore(7);
        line4.setScore(6);
        line5.setScore(5);
        line6.setScore(4);
        line7.setScore(3);
        line8.setScore(2);
        line9.setScore(1);

        startingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    // creates the playing scoreboard
    public void createPlaying(String arenaName){
        playingObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(ChatColor.YELLOW + "TntRun Game", "dummy");
        int playTime = this.plugin.playingCountUpMap.get(arenaName).getCountdownTime();
        int intSeconds = playTime % 60;
        String minutes = String.valueOf(Math.floorDiv(playTime,60));
        String strSeconds;


        if (intSeconds < 10){
            strSeconds = "0" + intSeconds;
        }
        else {
            strSeconds = "" + intSeconds;
        }

        Score line1 = playingObjective.getScore(ChatColor.YELLOW + "-----------------");
        Score line2 = playingObjective.getScore(ChatColor.YELLOW + "Map: " + arenaName);
        Score line3 = playingObjective.getScore(ChatColor.YELLOW + "");
        Score line4 = playingObjective.getScore(ChatColor.YELLOW +  "Duration: " + minutes + ":" + strSeconds);
        Score line5 = playingObjective.getScore(ChatColor.YELLOW + " ");
        Score line6 = playingObjective.getScore(ChatColor.YELLOW + "----------------- ");
        Score line7 = playingObjective.getScore(ChatColor.YELLOW + "yourserver.com");

        line1.setScore(7);
        line2.setScore(6);
        line3.setScore(5);
        line4.setScore(4);
        line5.setScore(3);
        line6.setScore(2);
        line7.setScore(1);

        playingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    // recreates the scoreboards and displays the correct one to the player
    public void refresh(String arenaName){
        String arenaState = this.plugin.gameStatusMap.get(arenaName);

        // check if in the lobby
        if (!this.plugin.playingMap.get(arenaName).contains(player) && !this.plugin.spectatingMap.get(arenaName).contains(player)){
            lobbyObjective.unregister();
            createLobby();
            player.setScoreboard(lobbyObjective.getScoreboard());
        }
        else if (!this.plugin.editingMap.get(arenaName).contains(player)){
            if (arenaState.equals("starting") || arenaState.equals("stopped")){
                startingObjective.unregister();
                createStarting(arenaName);
                player.setScoreboard(startingObjective.getScoreboard());
            }
            else if (arenaState.equals("playing")){
                playingObjective.unregister();
                createPlaying(arenaName);
                player.setScoreboard(playingObjective.getScoreboard());
            }
        }
    }
}

