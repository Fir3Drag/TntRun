package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardController {
    private final TntRun plugin;

    // stores players scoreboard
    private final Map<Player, Objective> scoreboardMap = new HashMap<>();

    public ScoreboardController(TntRun plugin) {
        this.plugin = plugin;
    }

    // handle the user written scoreboard from the config to get colours and keywords
    private void loadScoreboardConfig(String arenaName, String configName, Player player){
        List<String> startingScoreboardList = this.plugin.data.getScoreboardConfig().getStringList(configName);
        int scoreCount = startingScoreboardList.size();
        int stopCount = 1;  // if this goes above 15 it stops making lines

        // create the objective
        String title = this.plugin.data.getScoreboardConfig().getString(configName + "Title");
        title = ChatColor.translateAlternateColorCodes('&', title);

        if (title.length() > 16){  // prevents errors
            title = title.substring(0, 16);
        }

        // recreates objective
        if (scoreboardMap.containsKey(player)){
            scoreboardMap.get(player).unregister();
        }
        Objective objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(title, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // loop through each string and turn it into a scoreboard line
        for (String line: startingScoreboardList){
            if (stopCount > 15){
                break;
            }

            if (!arenaName.equals("lobby")){   // tries to replace arena
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

            int winCount = this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).getInt("winCount");
            line = line.replace("$winCount", String.valueOf(winCount));  // tries to get win count

            line = ChatColor.translateAlternateColorCodes('&', line);  // translate colors

            if (line.length() > 40){  // prevent errors
                line = line.substring(0, 40);
            }

            Score score = objective.getScore(line);
            score.setScore(scoreCount);

            scoreCount--;
            stopCount++;
        }

        // stores the created objective
        if (!scoreboardMap.containsKey(player)){
            scoreboardMap.put(player, objective);
        }
        else {
            scoreboardMap.replace(player, objective);
        }
    }

    // recreates the scoreboards and displays the correct one to the player
    public void refresh(String arenaName, Player player){
        if (this.plugin.lobbyList.contains(player)){  // check if in the lobby
            loadScoreboardConfig("lobby", "lobbyScoreboard", player);
            player.setScoreboard(scoreboardMap.get(player).getScoreboard());
        }

        else if (!arenaName.equals("lobby")){
            String arenaState = this.plugin.gameStatusMap.get(arenaName);

            if (!this.plugin.editingMap.get(arenaName).contains(player)){  // checks their not editing (editors get the lobby scoreboard)
                if (arenaState.equals("starting") || arenaState.equals("stopped")){
                    loadScoreboardConfig(arenaName, "startingScoreboard", player);
                    player.setScoreboard(scoreboardMap.get(player).getScoreboard());
                }
                else if (arenaState.equals("playing")){
                    loadScoreboardConfig(arenaName, "playingScoreboard", player);
                    player.setScoreboard(scoreboardMap.get(player).getScoreboard());
                }
            }
        }
    }

    // handles updating your scoreboard on server join
    public void handleJoin(Player player){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){
            refresh(arenaName, player);
        }
        else {
            refresh("lobby", player);
        }
    }

    // remove a player from scoreboard map on leave in case it is looped upon in the future
    // and a player not existing causes error
    public void handleQuit(Player player){
        scoreboardMap.remove(player);
    }
}

