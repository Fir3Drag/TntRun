package com.fir3drag.tntrun;

import com.fir3drag.tntrun.arena.commands.CommandManager;
import com.fir3drag.tntrun.arena.commands.ForceStartCommand;
import com.fir3drag.tntrun.arena.commands.LeaveCommand;
import com.fir3drag.tntrun.arena.commands.SpectateCommand;
import com.fir3drag.tntrun.arena.controllers.*;
import com.fir3drag.tntrun.arena.listeners.*;
import com.fir3drag.tntrun.arena.tasks.BlockRemoverTask;
import com.fir3drag.tntrun.arena.tasks.PlayingCountUpTask;
import com.fir3drag.tntrun.arena.tasks.StartingCountdownTask;
import com.fir3drag.tntrun.configuration.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class TntRun extends JavaPlugin {
    public TntRun plugin;
    public DataManager data;

    public CountdownController countdownController;
    public LobbyController lobbyController;
    public PermController permController;
    public PlayerMapsController playerMapsController;
    public SpectatorController spectatorController;
    public WinController winController;

    public BlockRemoverTask blockRemoverTask;

    public List<Player> lobbyEditList = new ArrayList<>();

    public Map<String, List<Player>> playingMap = new HashMap<>();
    public Map<String, List<Player>> spectatingMap = new HashMap<>();
    public Map<String, List<Player>> editingMap = new HashMap<>();
    public Map<String, String> gameStatusMap = new HashMap<>();
    public Map<String, StartingCountdownTask> startingCountdownMap = new HashMap<>();
    public Map<String, PlayingCountUpTask> playingCountUpMap = new HashMap<>();
    public Map<String, Map<Block, Material>> rollbackMap = new HashMap<>();
    public Map<Player, ScoreboardController> scoreboardMap = new HashMap<>();

    public void loadDefaultArenaValues(World arena){
        String arenaName = arena.getName();

        playingMap.put(arenaName, new ArrayList<>());
        spectatingMap.put(arenaName, new ArrayList<>());
        editingMap.put(arenaName, new ArrayList<>());
        gameStatusMap.put(arenaName, "stopped");
        startingCountdownMap.put(arenaName, new StartingCountdownTask(plugin, arena));
        playingCountUpMap.put(arenaName, new PlayingCountUpTask(plugin, arena));
        rollbackMap.put(arenaName, new HashMap<>());
    }

    public void removeDefaultArenaValues(String arenaName){
        playingMap.remove(arenaName);
        spectatingMap.remove(arenaName);
        editingMap.remove(arenaName);
        gameStatusMap.remove(arenaName);
        startingCountdownMap.remove(arenaName);
        playingCountUpMap.remove(arenaName);
        rollbackMap.remove(arenaName);
    }

    private void loadWorlds(){
        for (String arenaName: data.getDataConfig().getStringList("arenas")) {  // load in the worlds
            World arena = Bukkit.getWorld(arenaName);

            if (arena == null) {
                new WorldCreator(arenaName).createWorld();
                arena = Bukkit.getWorld(arenaName);
            }
            if (arena != null){  // check the world exists
                loadDefaultArenaValues(arena);
            }
        }
    }

    // on reload loads in the scoreboards for all online players to prevent errors
    private void loadScoreboards(){
        for (Player p: Bukkit.getOnlinePlayers()){
            // creates a scoreboard controller for each player that joins
            if (!this.plugin.scoreboardMap.containsKey(p)){
                this.plugin.scoreboardMap.put(p, new ScoreboardController(this.plugin, p));
            }
        }
    }

    /*
    TODO make tntrun join display a window where u can select arenas through an inventory
    TODO custom spectator stuff like bed to leave game and compass to tp to players
    TODO double jumps - store a map of players and how many double jumps they have (reset -> config amount) check if their in the air, add y velocity to player
    TODO maybe put a confirm on the delete command
    TODO could put all arenas in a single world (too many worlds might look confusing in the server folder) -> require setting positions to protect and stuff like that needing a /tr pos1 /tr pos2
    TODO single arena map which is then duplicated to host multiple at the same time

    TODO edit auto complete doesn't work

    TODO spectating config: (concerned that u might be able to body block as a spec)
    have night vision: true
    can see other spectators: true (might make this player decided with a custom item)
     */

    @Override
    public void onEnable() {
        getLogger().info("TntRun plugin started");

        // initialise variables
        plugin = this;

        data = new DataManager(this);

        // controllers
        countdownController = new CountdownController(this);
        lobbyController = new LobbyController(this);
        permController = new PermController();
        playerMapsController = new PlayerMapsController(this);
        spectatorController = new SpectatorController(this);
        winController = new WinController(this);

        // tasks
        blockRemoverTask = new BlockRemoverTask(this);

        // loads required to prevent null errors
        loadWorlds();  // makes sure there is a key for each world in the player maps
        loadScoreboards();  // makes sure there is a scoreboard for each player online on reload (as it is only handled on join/leave)

        // commands
        Objects.requireNonNull(getCommand("tntrun")).setExecutor(new CommandManager(this));
        Objects.requireNonNull(getCommand("forceStart")).setExecutor(new ForceStartCommand(this));
        Objects.requireNonNull(getCommand("leave")).setExecutor(new LeaveCommand(this));
        Objects.requireNonNull(getCommand("spectate")).setExecutor(new SpectateCommand(this));

        // listeners
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerHungerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WeatherListener(this), this);
    }

    @Override
    public void onDisable() {
        // tps all players out of worlds on restart to prevent any logic errors
        for (String arenaName: data.getDataConfig().getStringList("arenas")) {  // get worlds
            World arena = Bukkit.getWorld(arenaName);

            if (arena != null && !this.gameStatusMap.get(arenaName).isEmpty()){
                for (Player p : playingMap.get(arenaName)) { // send the players back to the lobby
                    p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                for (Player p : spectatingMap.get(arenaName)) { // send the spectators back to the lobby
                    p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                plugin.gameStatusMap.replace(arena.getName(), "restarting");

                // gets the rollback map and rolls the blocks back, then clears the map
                Map<Block, Material> rollbackMap = plugin.rollbackMap.get(arena.getName());

                for (Block b: rollbackMap.keySet()){
                    b.setType(rollbackMap.get(b));
                }
                rollbackMap.clear();
                plugin.rollbackMap.replace(arena.getName(), rollbackMap);

                plugin.gameStatusMap.replace(arena.getName(), "stopped");
            }
        }
        getLogger().info("TntRun plugin stopped");
    }
}
