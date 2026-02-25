package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.tasks.GameEndTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class GameController {
    private final TntRun plugin;

    public GameController(TntRun plugin) {
        this.plugin = plugin;
    }

    public void checkForWinner(String arenaName, Player player){  // checks if the game is over
        if (this.plugin.gameStatusMap.get(arenaName).equals("playing")) {
            if (this.plugin.playingMap.get(arenaName).size() > 1) {
                for (Player p : this.plugin.playingMap.get(arenaName)) { // msgs players
                    p.sendMessage(ChatColor.YELLOW  + "There are "  + this.plugin.playingMap.get(arenaName).size() + " left");
                }
                for (Player p : this.plugin.spectatingMap.get(arenaName)) { // msgs spectators
                    p.sendMessage(ChatColor.YELLOW  + "There are " + this.plugin.playingMap.get(arenaName).size() + " left");
                }
            }
            else {
                Player winner = this.plugin.playingMap.get(arenaName).get(0);  // gets the winner
                this.plugin.playingCountUpMap.get(arenaName).cancelCountdown();  // stop the timer

                for (Player p : this.plugin.playingMap.get(arenaName)) { // msgs players
                    p.sendMessage(ChatColor.YELLOW + winner.getDisplayName() + " has won!");
                    p.sendTitle(ChatColor.YELLOW  + winner.getDisplayName() + " has won!", ChatColor.YELLOW + "");
                    this.plugin.spectatorController.showAllPlayersYouAndYouAllPlayers(arenaName, p);
                }
                for (Player p : this.plugin.spectatingMap.get(arenaName)) { // msgs spectators
                    p.sendMessage(ChatColor.YELLOW + winner.getDisplayName() + " has won!");
                    p.sendTitle(ChatColor.YELLOW  + winner.getDisplayName() + " has won!", ChatColor.YELLOW + "");
                    this.plugin.spectatorController.showAllPlayersYouAndYouAllPlayers(arenaName, p);
                }

                int playerWinCount = this.plugin.data.getDataConfig().getConfigurationSection(winner.getUniqueId().toString()).getInt("winCount");
                this.plugin.data.getDataConfig().getConfigurationSection(winner.getUniqueId().toString()).set("winCount", ++playerWinCount);
                this.plugin.data.saveConfig();
                this.plugin.gameStatusMap.replace(arenaName, "gameEnded");
                new GameEndTask(plugin, player.getWorld());
            }
        }
    }

    // handles the player falling = elimination
    public void handlePlayerFall(Player player){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        long deathYLevel = this.plugin.data.getTntRunConfig().getInt("playerDeathYLevel");
        String arenaName = player.getWorld().getName();

        // handles player falling = lose game
        if (arenas.contains(arenaName)){ // checks your in an arena
            // checks your a player, the game is playing, you fell and that you're not the last player alive
            if (this.plugin.playingMap.get(arenaName).contains(player) && this.plugin.gameStatusMap.get(arenaName).equals("playing") &&
                    player.getLocation().getBlockY() < deathYLevel && this.plugin.playingMap.get(arenaName).size() > 1){

                for (Player p: this.plugin.playingMap.get(arenaName)){ // msgs all players
                    p.sendMessage(player.getDisplayName() + " has died.");
                }

                for (Player p: this.plugin.spectatingMap.get(arenaName)){ // msgs all spectators
                    p.sendMessage(player.getDisplayName() + " has died.");
                }
                this.plugin.playerMapsController.addToSpectating(arenaName, player);
                this.plugin.playerMapsController.removeFromPlaying(arenaName, player);
            }
        }
    }

    // handles tping players out of the void so they don't get stuck
    public void handleEnteringVoid(Player player){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        long voidYLevel = this.plugin.data.getTntRunConfig().getInt("voidYLevel");
        String arenaName = player.getWorld().getName();

        // handles players in void to tp them to world spawn
        if (arenas.contains(arenaName) && player.getLocation().getBlockY() < voidYLevel){  // player in arena
            if (this.plugin.spectatingMap.get(arenaName).contains(player) || this.plugin.playingMap.get(arenaName).contains(player)){  // checks they are in the void
                player.teleport(player.getWorld().getSpawnLocation()); // teleports them to world spawn
            }
        }
    }

    public void handleRejoin(Player player){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        int maxPlayers = this.plugin.data.getTntRunConfig().getInt("maxPlayers");
        World arena = player.getWorld();
        String arenaName = arena.getName();

        // handles players reconnecting into an arena
        if (arenas.contains(arenaName)){  // checks if its an arena
            // if the arena is full your set as a spectator on rejoin
            if (this.plugin.playingMap.get(arenaName).size() > maxPlayers && this.plugin.gameStatusMap.get(arenaName).equals("starting")){
                this.plugin.playerMapsController.addToSpectating(arenaName, player);
                player.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' you were in is full, you're now a spectator.");
                return;
            }

            // if the game state is starting or stopped add you as a player
            if (this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("stopped") ||
                    this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("starting")){
                this.plugin.playerMapsController.addToPlaying(arenaName, player);
            }

            // if the game state is now playing add you as a spectator
            if (this.plugin.gameStatusMap.get(player.getWorld().getName()).equals("playing")){
                this.plugin.playerMapsController.addToSpectating(arenaName, player);
            }

            // handles players joining during start phase
            this.plugin.countdownController.checkForStart(player.getWorld().getName(), -1);
        }
    }

    public void handleArenaInventoryClick(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player){
            Player player = ((Player) event.getWhoClicked()).getPlayer();

            List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
            String arenaName = player.getWorld().getName();

            if (arenas.contains(arenaName)){  // if their in an arena
                if (!this.plugin.editingMap.get(arenaName).contains(player)){  // and not editing it
                    event.setCancelled(true);
                }
            }
        }
    }

    public void handleQuit(Player player){
        // removes player from map lists when they disconnect to prevent errors
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        World arena = player.getWorld();
        String arenaName = arena.getName();

        if (arenas.contains(arenaName)){  // checks if its an arena and handles players leaving during start phase
            this.plugin.playerMapsController.removeAll(arenaName, player);
        }
    }

    public void handleTntRemover(Player player){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        String arenaName = player.getWorld().getName();

        // checks the player is in an arena and is playing the game
        if (arenas.contains(arenaName) && this.plugin.playingMap.get(arenaName).contains(player)){
            if (this.plugin.gameStatusMap.get(arenaName).equals("playing") && this.plugin.startingCountdownMap.get(arenaName).isGracePeriodOver()){  // checks the game has started
                Block blockUnderPlayer = player.getLocation().subtract(0, 1, 0).getBlock();
                Block secondBlockUnderPlayer = player.getLocation().subtract(0, 2, 0).getBlock();

                if (blockUnderPlayer.getType().equals(Material.SAND) || blockUnderPlayer.getType().equals(Material.GRAVEL)){
                    if (secondBlockUnderPlayer.getType().equals(Material.TNT)){
                        this.plugin.blockRemoverTask.triggerBlockRemove(arenaName, player.getLocation());
                    }
                }
            }
        }
    }

    public void handleBlockBreak(BlockBreakEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            if (!this.plugin.editingMap.get(arenaName).contains(player)){  // if they are in the list prevents you getting to the cancel event
                event.setCancelled(true);
            }
        }
    }

    public void handleBlockPlace(BlockPlaceEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            if (!this.plugin.editingMap.get(arenaName).contains(player)){  // if they are in the list prevents you getting to the cancel event
                event.setCancelled(true);
            }
        }
    }
}
