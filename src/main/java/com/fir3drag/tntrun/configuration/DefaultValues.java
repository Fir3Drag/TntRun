package com.fir3drag.tntrun.configuration;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class DefaultValues {
    private final TntRun plugin;

    // tntrun.yml
    private boolean disableDamage = true;
    private boolean disableWeather = true;
    private boolean disableHunger = true;
    private boolean disableNaturalCreatureSpawn = true;
    private boolean enableNightVision = true;

    private String joiningCompassName = ChatColor.translateAlternateColorCodes('&', "&2Join Arena");
    private String showPlayersName = ChatColor.translateAlternateColorCodes('&', "&2Show Players");
    private String hidePlayersName = ChatColor.translateAlternateColorCodes('&', "&2Hide Players");

    private int fullCountdown = 60;
    private int halfCountdown = 30;
    private int quarterCountdown = 15;

    private int forceStartCountdown = 5;

    private int requiredPlayersToStartCountdown = 2;
    private int requiredPlayersToStartHalfCountdown = 4;
    private int requiredPlayersToStartQuarterCountdown = 8;
    private int maxPlayers = 8;

    private boolean forcePlayerGameMode = true;
    private boolean setFullHealth = true;
    private boolean setFullHunger = true;

    private int gracePeriod = 2;
    private int tntRemoveDelay = 10;
    private int playerDeathYLevel = 0;
    private int voidYLevel = -20;

    private String spectatingCompassName = ChatColor.translateAlternateColorCodes('&', "&2Join Arena");
    private String playAgainName = ChatColor.translateAlternateColorCodes('&', "&2Join Arena");
    private String showSpectatorsName = ChatColor.translateAlternateColorCodes('&', "&2Join Arena");
    private String hideSpectatorsName = ChatColor.translateAlternateColorCodes('&', "&2Join Arena");
    private String leaveName = ChatColor.translateAlternateColorCodes('&', "&2Join Arena");

    // scoreboard.yml
    private String lobbyScoreboardTitle  = ChatColor.translateAlternateColorCodes('&', "&eTntRun Lobby");
    private List<String> lobbyScoreboard = Arrays.asList(
            ChatColor.translateAlternateColorCodes('&',"&e----------------"),
            ChatColor.translateAlternateColorCodes('&',"&eWins: $winCount"),
            ChatColor.translateAlternateColorCodes('&',"&e---------------- "),
            ChatColor.translateAlternateColorCodes('&',"&eserver.com")
    );

    private String startingScoreboardTitle  = ChatColor.translateAlternateColorCodes('&', "&eTntRun Queue");
    private List<String> startingScoreboard = Arrays.asList(
            ChatColor.translateAlternateColorCodes('&',"&e----------------"),
            ChatColor.translateAlternateColorCodes('&',"&eArena: $arena"),
            ChatColor.translateAlternateColorCodes('&'," "),
            ChatColor.translateAlternateColorCodes('&',"&eDuration: $duration"),
            ChatColor.translateAlternateColorCodes('&',"  "),
            ChatColor.translateAlternateColorCodes('&',"&ePlayers: $playerCount/$maxPlayers"),
            ChatColor.translateAlternateColorCodes('&',"&e---------------- "),
            ChatColor.translateAlternateColorCodes('&',"&eserver.com")
    );

    private String playingScoreboardTitle  = ChatColor.translateAlternateColorCodes('&', "&eTntRun Game");
    private List<String> playingScoreboard = Arrays.asList(
            ChatColor.translateAlternateColorCodes('&',"&e----------------"),
            ChatColor.translateAlternateColorCodes('&',"&eMap: $arena"),
            ChatColor.translateAlternateColorCodes('&'," "),
            ChatColor.translateAlternateColorCodes('&',"&eDuration: $duration"),
            ChatColor.translateAlternateColorCodes('&',"  "),
            ChatColor.translateAlternateColorCodes('&',"&ePlayers left: $playerCount"),
            ChatColor.translateAlternateColorCodes('&',"&e---------------- "),
            ChatColor.translateAlternateColorCodes('&', "&eserver.com")
    );

    // data.yml
    private List<String> arenas = new ArrayList<>();
    private List<String> disabledArenas = new ArrayList<>();

    public DefaultValues(TntRun plugin) {
        this.plugin = plugin;

        loadTntRunConfig();
        loadScoreboardConfig();
        loadDataConfig();
    }

    // loads all tntrun values to make sure that they exist preventing null errors
    public void loadTntRunConfig(){
        if (this.plugin.data.getTntRunConfig().get("disableDamage") != null){
            disableDamage = this.plugin.data.getTntRunConfig().getBoolean("disableDamage");
        }
        if (this.plugin.data.getTntRunConfig().get("disableWeather") != null){
            disableWeather = this.plugin.data.getTntRunConfig().getBoolean("disableWeather");
        }
        if (this.plugin.data.getTntRunConfig().get("disableHunger") != null){
            disableHunger = this.plugin.data.getTntRunConfig().getBoolean("disableHunger");
        }
        if (this.plugin.data.getTntRunConfig().get("disableNaturalCreatureSpawn") != null){
            disableNaturalCreatureSpawn = this.plugin.data.getTntRunConfig().getBoolean("disableNaturalCreatureSpawn");
        }
        if (this.plugin.data.getTntRunConfig().get("enableNightVision") != null){
            enableNightVision = this.plugin.data.getTntRunConfig().getBoolean("enableNightVision");
        }

        String tempJoiningCompassName = this.plugin.data.getTntRunConfig().getString("joiningCompassName");

        if (tempJoiningCompassName != null){
            joiningCompassName = ChatColor.translateAlternateColorCodes('&', tempJoiningCompassName);
        }

        String tempShowPlayersName = this.plugin.data.getTntRunConfig().getString("showPlayersName");

        if (tempShowPlayersName != null){
            showPlayersName = ChatColor.translateAlternateColorCodes('&', tempShowPlayersName);
        }

        String tempHidePlayersName = this.plugin.data.getTntRunConfig().getString("hidePlayersName");

        if (tempHidePlayersName != null){
            hidePlayersName = ChatColor.translateAlternateColorCodes('&', tempHidePlayersName);
        }

        if (this.plugin.data.getTntRunConfig().get("fullCountdown") != null){
            fullCountdown = this.plugin.data.getTntRunConfig().getInt("fullCountdown");
        }
        if (this.plugin.data.getTntRunConfig().get("halfCountdown") != null){
            halfCountdown = this.plugin.data.getTntRunConfig().getInt("halfCountdown");
        }
        if (this.plugin.data.getTntRunConfig().get("quarterCountdown") != null){
            quarterCountdown = this.plugin.data.getTntRunConfig().getInt("quarterCountdown");
        }

        if (this.plugin.data.getTntRunConfig().get("forceStartCountdown") != null){
            forceStartCountdown = this.plugin.data.getTntRunConfig().getInt("forceStartCountdown");
        }

        if (this.plugin.data.getTntRunConfig().get("requiredPlayersToStartCountdown") != null){
            requiredPlayersToStartCountdown = this.plugin.data.getTntRunConfig().getInt("requiredPlayersToStartCountdown");
        }
        if (this.plugin.data.getTntRunConfig().get("requiredPlayersToStartHalfCountdown") != null){
            requiredPlayersToStartHalfCountdown = this.plugin.data.getTntRunConfig().getInt("requiredPlayersToStartHalfCountdown");
        }
        if (this.plugin.data.getTntRunConfig().get("requiredPlayersToStartQuarterCountdown") != null){
            requiredPlayersToStartQuarterCountdown = this.plugin.data.getTntRunConfig().getInt("requiredPlayersToStartQuarterCountdown");
        }
        if (this.plugin.data.getTntRunConfig().get("maxPlayers") != null){
            maxPlayers = this.plugin.data.getTntRunConfig().getInt("maxPlayers");
        }

        if (this.plugin.data.getTntRunConfig().get("forcePlayerGameMode") != null){
            forcePlayerGameMode = this.plugin.data.getTntRunConfig().getBoolean("forcePlayerGameMode");
        }
        if (this.plugin.data.getTntRunConfig().get("setFullHealth") != null){
            setFullHealth = this.plugin.data.getTntRunConfig().getBoolean("setFullHealth");
        }
        if (this.plugin.data.getTntRunConfig().get("setFullHunger") != null){
            setFullHunger = this.plugin.data.getTntRunConfig().getBoolean("setFullHunger");
        }

        if (this.plugin.data.getTntRunConfig().get("gracePeriod") != null){
            gracePeriod = this.plugin.data.getTntRunConfig().getInt("gracePeriod");
        }
        if (this.plugin.data.getTntRunConfig().get("tntRemoveDelay") != null){
            tntRemoveDelay = this.plugin.data.getTntRunConfig().getInt("tntRemoveDelay");
        }
        if (this.plugin.data.getTntRunConfig().get("playerDeathYLevel") != null){
            playerDeathYLevel = this.plugin.data.getTntRunConfig().getInt("playerDeathYLevel");
        }
        if (this.plugin.data.getTntRunConfig().get("voidYLevel") != null){
            voidYLevel = this.plugin.data.getTntRunConfig().getInt("voidYLevel");
        }

        String tempSpectatingCompassName = this.plugin.data.getTntRunConfig().getString("spectatingCompassName");

        if (tempSpectatingCompassName != null){
            spectatingCompassName = ChatColor.translateAlternateColorCodes('&', tempSpectatingCompassName);
        }

        String tempPlayAgainName = this.plugin.data.getTntRunConfig().getString("playAgainName");

        if (tempPlayAgainName != null){
            playAgainName = ChatColor.translateAlternateColorCodes('&', tempPlayAgainName);
        }

        String tempShowSpectatorsName = this.plugin.data.getTntRunConfig().getString("showSpectatorsName");

        if (tempShowSpectatorsName != null){
            showSpectatorsName = ChatColor.translateAlternateColorCodes('&', tempShowSpectatorsName);
        }

        String tempHideSpectatorsName = this.plugin.data.getTntRunConfig().getString("hideSpectatorsName");

        if (tempHideSpectatorsName != null){
            hideSpectatorsName = ChatColor.translateAlternateColorCodes('&', tempHideSpectatorsName);
        }

        String tempLeaveName = this.plugin.data.getTntRunConfig().getString("leaveName");

        if (tempLeaveName != null){
            leaveName = ChatColor.translateAlternateColorCodes('&', tempLeaveName);
        }
    }

    public void loadScoreboardConfig(){
        String tempLobbyScoreboardTitle = this.plugin.data.getScoreboardConfig().getString("lobbyScoreboardTitle");

        if (tempLobbyScoreboardTitle != null){
            lobbyScoreboardTitle = ChatColor.translateAlternateColorCodes('&', tempLobbyScoreboardTitle);
        }

        List<String> tempLobbyScoreboard = this.plugin.data.getScoreboardConfig().getStringList("lobbyScoreboard");

        if (tempLobbyScoreboard != null){
            lobbyScoreboard = new ArrayList<>();

            for (String s: tempLobbyScoreboard){
                lobbyScoreboard.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }

        String tempStartingScoreboardTitle = this.plugin.data.getScoreboardConfig().getString("startingScoreboardTitle");

        if (tempStartingScoreboardTitle != null){
            startingScoreboardTitle = ChatColor.translateAlternateColorCodes('&', tempStartingScoreboardTitle);
        }

        List<String> tempStartingScoreboard = this.plugin.data.getScoreboardConfig().getStringList("startingScoreboard");

        if (tempStartingScoreboard != null){
            startingScoreboard = new ArrayList<>();

            for (String s: tempStartingScoreboard){
                startingScoreboard.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }

        String tempPlayingScoreboardTitle = this.plugin.data.getScoreboardConfig().getString("playingScoreboardTitle");

        if (tempPlayingScoreboardTitle != null){
            playingScoreboardTitle = ChatColor.translateAlternateColorCodes('&', tempPlayingScoreboardTitle);
        }

        List<String> tempPlayingScoreboard = this.plugin.data.getScoreboardConfig().getStringList("playingScoreboard");

        if (tempPlayingScoreboard != null){
            playingScoreboard = new ArrayList<>();

            for (String s: tempPlayingScoreboard){
                playingScoreboard.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }

    public void loadDataConfig(){
        List<String> tempArenas = this.plugin.data.getDataConfig().getStringList("arenas");

        if (tempArenas != null){
            arenas = tempArenas;
        }

        List<String> tempDisabledArenas = this.plugin.data.getDataConfig().getStringList("disabledArenas");

        if (tempDisabledArenas != null){
            disabledArenas = tempDisabledArenas;
        }
    }

    // handles data.yml to create the player values
    public void handleJoin(Player player){
        // creates the players section
        if (!this.plugin.data.getDataConfig().isConfigurationSection(player.getUniqueId().toString())){
            this.plugin.data.getDataConfig().createSection(player.getUniqueId().toString());
        }

        // check that default values exist
        if (this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).get("lobbyPlayersVisible") == null){
            this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("lobbyPlayersVisible", true);
        }

        if (this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).get("spectatorPlayersVisible") == null){
            this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("spectatorPlayersVisible", true);
        }

        if (this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).get("winCount") == null){
            this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("winCount", 0);
        }
        this.plugin.data.saveConfig();
    }

    // tntrun.yml
    public Boolean getDisableDamage(){
        return disableDamage;
    }

    public Boolean getDisableWeather(){
        return disableWeather;
    }

    public Boolean getDisableHunger(){
        return disableHunger;
    }

    public Boolean getDisableNaturalCreatureSpawn(){
        return disableNaturalCreatureSpawn;
    }

    public Boolean getEnableNightVision(){
        return enableNightVision;
    }

    public String getJoiningCompassName(){
        return joiningCompassName;
    }

    public String getShowPlayersName(){
        return showPlayersName;
    }

    public String getHidePlayersName(){
        return hidePlayersName;
    }

    public Integer getFullCountdown(){
        return fullCountdown;
    }

    public Integer getHalfCountdown(){
        return halfCountdown;
    }

    public Integer getQuarterCountdown(){
        return quarterCountdown;
    }

    public Integer getForceStartCountdown(){
        return forceStartCountdown;
    }

    public Integer getRequiredPlayersToStartCountdown(){
        return requiredPlayersToStartCountdown;
    }

    public Integer getRequiredPlayersToStartHalfCountdown(){
        return requiredPlayersToStartHalfCountdown;
    }

    public Integer getRequiredPlayersToStartQuarterCountdown(){
        return requiredPlayersToStartQuarterCountdown;
    }

    public Integer getMaxPlayers(){
        return maxPlayers;
    }

    public Boolean getForcePlayerGameMode(){
        return forcePlayerGameMode;
    }

    public Boolean getSetFullHealth(){
        return setFullHealth;
    }

    public Boolean getSetFullHunger(){
        return setFullHunger;
    }

    public Integer getGracePeriod(){
        return gracePeriod;
    }

    public Integer getTntRemoveDelay(){
        return tntRemoveDelay;
    }

    public Integer getPlayerDeathYLevel(){
        return playerDeathYLevel;
    }

    public Integer getVoidYLevel(){
        return voidYLevel;
    }

    public String getSpectatingCompassName(){
        return spectatingCompassName;
    }

    public String getPlayAgainName(){
        return playAgainName;
    }

    public String getShowSpectatorsName(){
        return showSpectatorsName;
    }

    public String getHideSpectatorsName(){
        return hideSpectatorsName;
    }

    public String getLeaveName(){
        return leaveName;
    }

    // scoreboard.yml
    public List<String> getScoreboard(String scoreboardName){
        switch (scoreboardName) {
            case "lobbyScoreboard":
                return lobbyScoreboard;
            case "startingScoreboard":
                return startingScoreboard;
            case "playingScoreboard":
                return playingScoreboard;
        }
        return new ArrayList<>();
    }

    public String getScoreboardTitle(String scoreboardName){
        switch (scoreboardName) {
            case "lobbyScoreboard":
                return lobbyScoreboardTitle;
            case "startingScoreboard":
                return startingScoreboardTitle;
            case "playingScoreboard":
                return playingScoreboardTitle;
        }
        return "";
    }

    // data.yml
    public List<String> getArenas(){
        return new ArrayList<>(arenas);
    }

    public List<String> getDisabledArenas(){
        return new ArrayList<>(disabledArenas);
    }
}
