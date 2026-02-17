package com.fir3drag.tntrun;

import com.fir3drag.tntrun.arena.*;
import com.fir3drag.tntrun.arena.commands.CommandManager;
import com.fir3drag.tntrun.arena.listeners.*;
import com.fir3drag.tntrun.arena.tasks.BlockRemoverTask;
import com.fir3drag.tntrun.arena.tasks.CountdownTask;
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

    public ChangePlayerMaps changePlayerMaps;
    public CheckPerms checkPerms;
    public CustomSpectator customSpectator;
    public CheckForWinner checkForWinner;
    public Countdown countdown;
    public BlockRemoverTask blockRemoverTask;

    public Map<String, List<Player>> playingMap = new HashMap<>();
    public Map<String, List<Player>> spectatingMap = new HashMap<>();
    public Map<String, List<Player>> editingMap = new HashMap<>();
    public Map<String, String> gameStatusMap = new HashMap<>();
    public Map<String, CountdownTask> countdownMap = new HashMap<>();
    public Map<String, Map<Block, Material>> rollbackMap = new HashMap<>();

    public void loadWorlds(){
        for (String worldName: data.getDataConfig().getStringList("arenas")) {  // load in the worlds
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                new WorldCreator(worldName).createWorld();
                world = Bukkit.getWorld(worldName);
            }
            if (world != null){  // check the world exists
                this.plugin.playingMap.put(worldName, new ArrayList<>());
                this.plugin.spectatingMap.put(worldName, new ArrayList<>());
                this.plugin.editingMap.put(worldName, new ArrayList<>());
                this.plugin.gameStatusMap.put(worldName, "stopped");
                this.plugin.countdownMap.put(worldName, new CountdownTask(plugin, world));
                this.plugin.rollbackMap.put(worldName, new HashMap<>());
            }
        }
    }

    /*
    TODO create a leaderboard for countdown so chat isn't spammed
    TODO make tntrun join display a window where u can select arenas through an inventory
    TODO custom spectator stuff like bed to leave game and compass to tp to players
    TODO double jumps - store a map of players and how many double jumps they have (reset -> config amount) check if their in the air, add y velocity to player
    TODO maybe put a confirm on the delete command
    TODO could put all arenas in a single world (too many worlds might look confusing in the server folder) -> require setting positions to protect and stuff like that needing a /tr pos1 /tr pos2

    TODO make a /tr spec command instead of /tr join
    TODO test concurrent games

    TODO spectating config:
    have night vision: true
    can see other spectators: true (might make this player decided with a custom item)
     */

    @Override
    public void onEnable() {
        getLogger().info("TntRun plugin started");

        // initialise variables
        plugin = this;
        data = new DataManager(this);

        changePlayerMaps = new ChangePlayerMaps(this);
        checkPerms = new CheckPerms();
        customSpectator = new CustomSpectator(this);
        checkForWinner = new CheckForWinner(this);
        countdown = new Countdown(this);
        blockRemoverTask = new BlockRemoverTask(this);
        loadWorlds();

        // commands
        Objects.requireNonNull(getCommand("tntrun")).setExecutor(new CommandManager(this));

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
