package com.fir3drag.tntrun;

import com.fir3drag.tntrun.arena.commands.*;
import com.fir3drag.tntrun.arena.controllers.*;
import com.fir3drag.tntrun.arena.listeners.*;
import com.fir3drag.tntrun.arena.tasks.BlockRemoverTask;
import com.fir3drag.tntrun.arena.tasks.PlayingCountUpTask;
import com.fir3drag.tntrun.arena.tasks.StartingCountdownTask;
import com.fir3drag.tntrun.configuration.DataManager;
import com.fir3drag.tntrun.configuration.DefaultValues;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class TntRun extends JavaPlugin {
    public boolean isShuttingDown;

    public TntRun plugin;
    public DataManager data;
    public DefaultValues defaultValues;

    public CountdownController countdownController;
    public GameController gameController;
    public ItemController itemController;
    public LobbyController lobbyController;
    public PermController permController;
    public PlayerMapsController playerMapsController;
    public ScoreboardController scoreboardController;
    public WorldController worldController;

    public BlockRemoverTask blockRemoverTask;

    public List<Player> lobbyList = new ArrayList<>();
    public List<Player> lobbyEditList = new ArrayList<>();

    public Map<String, List<Player>> playingMap = new HashMap<>();
    public Map<String, List<Player>> spectatingMap = new HashMap<>();
    public Map<String, List<Player>> editingMap = new HashMap<>();
    public Map<String, String> gameStatusMap = new HashMap<>();
    public Map<String, StartingCountdownTask> startingCountdownMap = new HashMap<>();
    public Map<String, PlayingCountUpTask> playingCountUpMap = new HashMap<>();
    public Map<String, Map<Block, Material>> rollbackMap = new HashMap<>();

    public void loadControllers(){
        countdownController = new CountdownController(this);
        gameController = new GameController(this);
        itemController = new ItemController(this);
        lobbyController = new LobbyController(this);
        permController = new PermController();
        playerMapsController = new PlayerMapsController(this);
        scoreboardController = new ScoreboardController(this);
        worldController = new WorldController(this);
    }

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
        for (String arenaName: defaultValues.getArenas()) {  // load in the worlds
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

    private void loadCommands(){
        Objects.requireNonNull(getCommand("tntrun")).setExecutor(new CommandManager(this));
        Objects.requireNonNull(getCommand("forceStart")).setExecutor(new ForceStartCommand(this));
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(this));
        Objects.requireNonNull(getCommand("leave")).setExecutor(new LeaveCommand(this));
        Objects.requireNonNull(getCommand("spectate")).setExecutor(new SpectateCommand(this));
    }

    private void loadListeners(){
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEntityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerToggleSneakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WeatherChangeListener(this), this);
    }

    // all players are sent to lobby on reload so all get lobby scoreboard
    private void preventReloadErrors(){
        lobbyList.addAll(Bukkit.getOnlinePlayers());  // makes sure that all players are put in the lobby list on reload/start

        for (Player p: Bukkit.getOnlinePlayers()){
            this.defaultValues.handleJoin(p);
            this.plugin.worldController.handlePlayerJoin(p);  // gives night vision on reload
            scoreboardController.refresh("lobby", p);  // makes sure all players get scoreboard on /reload
        }
        this.lobbyController.createJoiningArenaInventory();
    }

    /*
    TODO double jumps - store a map of players and how many double jumps they have (reset -> config amount) check if their in the air, add y velocity to player
    TODO maybe put a confirm on the delete command
    TODO could put all arenas in a single world (too many worlds might look confusing in the server folder) -> require setting positions to protect and stuff like that needing a /tr pos1 /tr pos2
    TODO single arena map which is then duplicated to host multiple at the same time
    TODO consider using slime world dependency for better loading of mini worlds
    TODO add a page scrolling system to the GUIs (spectating / arena invs)
    TODO can tp in spec mode (puts u in adventure when u tp)
     */

    @Override
    public void onEnable() {
        isShuttingDown = false;  // handles run later tasks to prevent errors
        getLogger().info("TntRun plugin started");

        // initialise variables
        plugin = this;
        data = new DataManager(this);
        defaultValues = new DefaultValues(this);

        blockRemoverTask = new BlockRemoverTask(this);  // tasks

        loadControllers();  // controllers
        loadWorlds();  // makes sure there is a key for each world in the player maps
        loadCommands();  // commands
        loadListeners();  // listeners
        preventReloadErrors();  // prevents null errors on /reload for players in the server
    }

    @Override
    public void onDisable() {
        isShuttingDown = true;  // handles run later tasks to prevent errors
        defaultValues = new DefaultValues(this);

        // tps all players out of worlds on restart to prevent any logic errors
        for (String arenaName: defaultValues.getArenas()) {  // get worlds
            World arena = Bukkit.getWorld(arenaName);

            if (arena != null && !this.gameStatusMap.get(arenaName).isEmpty()){

                for (Player p: Bukkit.getOnlinePlayers()){  // tp everyone to lobby
                    lobbyController.tp(p);
                    p.setGameMode(GameMode.SURVIVAL);
                    p.setAllowFlight(false);
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
