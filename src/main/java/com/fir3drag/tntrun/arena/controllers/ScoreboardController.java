package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

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

        loadScoreboardConfig("", "lobbyScoreboard");
        player.setScoreboard(lobbyObjective.getScoreboard());
    }

    // handle the user written scoreboard from the config to get colours and keywords
    private void loadScoreboardConfig(String arenaName, String configName){
        List<String> startingScoreboardList = this.plugin.data.getScoreboardConfig().getStringList(configName);
        int scoreCount = startingScoreboardList.size();
        int stopCount = 1;  // if this goes above 15 it stops making lines

        // create the objective
        String title = this.plugin.data.getScoreboardConfig().getString(configName + "Title");
        title = ChatColor.translateAlternateColorCodes('&', title);

        if (title.length() > 16){  // prevents errors
            title = title.substring(0, 16);
        }

        switch (configName) {  // overrides the correct objective
            case "lobbyScoreboard":
                lobbyObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(title, "dummy");
                lobbyObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                break;
            case "startingScoreboard":
                startingObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(title, "dummy");
                startingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                break;
            case "playingScoreboard":
                playingObjective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(title, "dummy");
                playingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                break;
        }

        for (String line: startingScoreboardList){  // loop through each string and turn it into a scoreboard line
            if (stopCount > 15){
                break;
            }

            if (!arenaName.isEmpty()){   // tries to replace arena
                // keyword variables
                String duration = String.valueOf(this.plugin.startingCountdownMap.get(arenaName).getCountdownTime());
                int playerCount = this.plugin.playingMap.get(arenaName).size();
                int maxPlayers = this.plugin.data.getTntRunConfig().getInt("maxPlayers");

                int playTime = this.plugin.playingCountUpMap.get(arenaName).getCountdownTime();
                int intSeconds = playTime % 60;
                String minutes = String.valueOf(Math.floorDiv(playTime,60));
                String strSeconds;

                // make the time have a 0 if its single digit
                if (intSeconds < 10){
                    strSeconds = "0" + intSeconds;
                }
                else {
                    strSeconds = "" + intSeconds;
                }

                // handles the starting count being paused
                if (duration.equals("-1") || !this.plugin.startingCountdownMap.get(arenaName).isCounting()){
                    duration = "Waiting for players";
                }

                line = line.replace("$arena", arenaName);
                line = line.replace("$playerCount", String.valueOf(playerCount));  // tries to get player count
                line = line.replace("$maxPlayers", String.valueOf(maxPlayers));  // tries to get player count

                if (configName.equals("startingScoreboard")){  // tries to get countdown starting time / countup time
                    line = line.replace("$duration", duration);
                }
                else if (configName.equals("playingScoreboard")){
                    line = line.replace("$duration", minutes + ":" + strSeconds);
                }
            }

            int winCount = this.plugin.data.getDataConfig().getInt(player.getUniqueId().toString());
            line = line.replace("$winCount", String.valueOf(winCount));  // tries to get win count

            line = ChatColor.translateAlternateColorCodes('&', line);  // translate colors

            if (line.length() > 40){  // prevent errors
                line = line.substring(0, 40);
            }

            // creates the scoreboard line
            Score score = null;

            switch (configName) {
                case "lobbyScoreboard":
                    score = lobbyObjective.getScore(line);
                    break;
                case "startingScoreboard":
                    score = startingObjective.getScore(line);
                    break;
                case "playingScoreboard":
                    score = playingObjective.getScore(line);
                    break;
            }

            if (score != null){
                score.setScore(scoreCount);
            }

            scoreCount--;
            stopCount++;
        }
    }

    // recreates the scoreboards and displays the correct one to the player
    public void refresh(String arenaName){
        String arenaState = this.plugin.gameStatusMap.get(arenaName);

        // check if in the lobby
        if (!this.plugin.playingMap.get(arenaName).contains(player) && !this.plugin.spectatingMap.get(arenaName).contains(player)){
            lobbyObjective.unregister();
            loadScoreboardConfig(arenaName, "lobbyScoreboard");
            player.setScoreboard(lobbyObjective.getScoreboard());
        }
        else if (!this.plugin.editingMap.get(arenaName).contains(player)){
            if (arenaState.equals("starting") || arenaState.equals("stopped")){
                startingObjective.unregister();
                loadScoreboardConfig(arenaName, "startingScoreboard");
                player.setScoreboard(startingObjective.getScoreboard());
            }
            else if (arenaState.equals("playing")){
                playingObjective.unregister();
                loadScoreboardConfig(arenaName, "playingScoreboard");
                player.setScoreboard(playingObjective.getScoreboard());
            }
        }
    }
}

