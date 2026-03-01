package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.tasks.GameEndTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {
    private final TntRun plugin;

    private final Map<String, Inventory> spectatingCompassInventoryMap = new HashMap<>();

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
                    showAllPlayersYouAndYouAllPlayers(arenaName, p);
                    this.plugin.scoreboardController.refresh(arenaName, p);
                }
                for (Player p : this.plugin.spectatingMap.get(arenaName)) { // msgs spectators
                    p.sendMessage(ChatColor.YELLOW + winner.getDisplayName() + " has won!");
                    p.sendTitle(ChatColor.YELLOW  + winner.getDisplayName() + " has won!", ChatColor.YELLOW + "");
                    showAllPlayersYouAndYouAllPlayers(arenaName, p);
                    this.plugin.scoreboardController.refresh(arenaName, p);
                }

                int playerWinCount = this.plugin.data.getDataConfig().getConfigurationSection(winner.getUniqueId().toString()).getInt("winCount");
                this.plugin.data.getDataConfig().getConfigurationSection(winner.getUniqueId().toString()).set("winCount", ++playerWinCount);
                this.plugin.data.saveConfig();
                this.plugin.gameStatusMap.replace(arenaName, "gameOver");
                plugin.lobbyController.createJoiningArenaInventory();
                new GameEndTask(plugin, player.getWorld());
            }
        }
    }

    // handles the player falling = elimination
    public void handlePlayerFall(Player player){
        List<String> arenas = this.plugin.defaultValues.getArenas();
        long deathYLevel = this.plugin.defaultValues.getPlayerDeathYLevel();
        String arenaName = player.getWorld().getName();

        // handles player falling = lose game
        if (arenas.contains(arenaName)){ // checks your in an arena
            // checks you're a player, the game is playing, you fell and that you're not the last player alive
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
        List<String> arenas = this.plugin.defaultValues.getArenas();
        long voidYLevel = this.plugin.defaultValues.getVoidYLevel();
        String arenaName = player.getWorld().getName();

        // handles players in void to tp them to world spawn
        if (arenas.contains(arenaName) && player.getLocation().getBlockY() < voidYLevel){  // player in arena
            if (this.plugin.spectatingMap.get(arenaName).contains(player) || this.plugin.playingMap.get(arenaName).contains(player)){  // checks they are in the void
                this.plugin.worldController.tpCenterOfBlock(player, player.getWorld().getSpawnLocation());
            }
        }
    }

    public void handleRejoin(Player player){
        List<String> arenas = this.plugin.defaultValues.getArenas();
        int maxPlayers = this.plugin.defaultValues.getMaxPlayers();
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
            this.plugin.countdownController.checkForStart(player.getWorld().getName());
        }
    }

    public void handlePlayerInteractEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        List<String> arenas = this.plugin.defaultValues.getArenas();
        String arenaName = player.getWorld().getName();

        if (event.getRightClicked() instanceof Player){
            Player target = (Player) event.getRightClicked();

            // check if in an arena and your a spectator and the target is a player
            if (arenas.contains(arenaName) && target != null){
                if (this.plugin.spectatingMap.get(arenaName).contains(player) && this.plugin.playingMap.get(arenaName).contains(target)){
                    player.setGameMode(GameMode.SPECTATOR);
                    player.setSpectatorTarget(target);
                }
            }
        }
    }

    public void handlePlayerToggleSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        List<String> arenas = this.plugin.defaultValues.getArenas();
        String arenaName = player.getWorld().getName();

        // puts you back in adventure mode when you stop spectating a player
        if (arenas.contains(arenaName)){
            if (this.plugin.spectatingMap.get(arenaName).contains(player)){  // checks your a spectator in an arena
                if (player.getGameMode() == GameMode.SPECTATOR){
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setAllowFlight(true);
                }
            }
        }
    }

    public void handleQuit(Player player){
        // removes player from map lists when they disconnect to prevent errors
        List<String> arenas = this.plugin.defaultValues.getArenas();
        World arena = player.getWorld();
        String arenaName = arena.getName();

        if (arenas.contains(arenaName)){  // checks if its an arena and handles players leaving during start phase
            this.plugin.playerMapsController.removeAll(arenaName, player);
        }
    }

    public void handleTntRemover(Player player){
        List<String> arenas = this.plugin.defaultValues.getArenas();
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
        List<String> arenas = this.plugin.defaultValues.getArenas();
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            if (!this.plugin.editingMap.get(arenaName).contains(player)){  // if they are in the list prevents you getting to the cancel event
                event.setCancelled(true);
            }
        }
    }

    public void handleBlockPlace(BlockPlaceEvent event){
        List<String> arenas = this.plugin.defaultValues.getArenas();
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (arenas.contains(arenaName)){  // checks its an arena
            if (!this.plugin.editingMap.get(arenaName).contains(player)){  // if they are in the list prevents you getting to the cancel event
                event.setCancelled(true);
            }
        }
    }

    // hide this player from all "playing" players, show spectators to this player
    public void setSpectator(String arenaName, Player player){  // custom spectator
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        hideYouFromPlayers(arenaName, player);
        showSpectators(arenaName, player);
        setSpectatorInventory(player);
    }

    public void setPlayer(Player player){
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
    }

    // show this player to all and show all to this player
    public void showAllPlayersYouAndYouAllPlayers(String arenaName, Player player){
        showPlayersYou(arenaName, player);
        showSpectators(arenaName, player);
    }

    public void showSpectators(String arenaName, Player player){
        for (Player p: this.plugin.spectatingMap.get(arenaName)){  // show the players spectating
            if (p != player){
                player.showPlayer(p);
            }
        }
    }

    public void hideSpectators(String arenaName, Player player){
        for (Player p: this.plugin.spectatingMap.get(arenaName)){  // hide all spectators from this player
            if (p != player){
                player.hidePlayer(p);
            }
        }
    }

    public void showPlayersYou(String arenaName, Player player){
        for (Player p: this.plugin.playingMap.get(arenaName)){  // show the players spectating
            if (p != player){
                p.showPlayer(player);
            }
        }
    }

    public void hideYouFromPlayers(String arenaName, Player player){
        for (Player p: this.plugin.playingMap.get(arenaName)){  // hide all spectators from this player
            if (p != player){
                p.hidePlayer(player);
            }
        }
    }

    public void setPlayingInventory(Player player){
        ItemStack[] invContents = new ItemStack[36];
        player.getInventory().setContents(invContents);
        player.updateInventory();
    }

    public void setSpectatorInventory(Player player){
        ItemStack[] invContents = new ItemStack[36];

        // puts the items in the inventory
        invContents[0] = this.plugin.itemController.getSpectatingCompass();
        invContents[4] = this.plugin.itemController.getPlayAgain();

        if (this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).getBoolean("spectatorPlayersVisible")){
            invContents[7] = this.plugin.itemController.getHideSpectators();
        }
        else {
            invContents[7] = this.plugin.itemController.getShowSpectators();
            this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("spectatorPlayersVisible", false);
        }

        invContents[8] = this.plugin.itemController.getLeave();

        player.getInventory().setContents(invContents);
        player.updateInventory();
    }

    public void setStartingInventory(Player player){
        ItemStack[] invContents = new ItemStack[36];
        invContents[8] = this.plugin.itemController.getLeave();
        player.getInventory().setContents(invContents);
        player.updateInventory();
    }

    // called on player join to an arena
    public void createSpectatingCompassInventory(String arenaName){
        if (!spectatingCompassInventoryMap.containsKey(arenaName)){
            spectatingCompassInventoryMap.put(arenaName, Bukkit.createInventory(null, 36, "Spectate Player"));
        }
        spectatingCompassInventoryMap.get(arenaName).clear();

        for (Player p: this.plugin.playingMap.get(arenaName)){  // creates a player head for each player playing
            ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwner(p.getName());
            meta.setDisplayName(ChatColor.YELLOW + p.getName());
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Left click to teleport", ChatColor.GRAY + "Right click to watch from their perspective"));

            playerHead.setItemMeta(meta);
            spectatingCompassInventoryMap.get(arenaName).addItem(playerHead);
        }
    }

    public void openSpectatingInventory(Player player){
        String arenaName = player.getWorld().getName();
        List<String> arenas = this.plugin.defaultValues.getArenas();

        if (arenas.contains(arenaName)){
            player.openInventory(spectatingCompassInventoryMap.get(arenaName));
        }
    }

    // prevent spectators pressing 1 to tp to other players
    public void handlePlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();
        List<String> arenas = this.plugin.defaultValues.getArenas();

        if (arenas.contains(arenaName)){
            if (this.plugin.spectatingMap.get(arenaName).contains(player)){
                if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // handles spectator teleports kinda
    public void handlePlayerTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();
        List<String> arenas = this.plugin.defaultValues.getArenas();

        if (arenas.contains(arenaName)){
            if (this.plugin.spectatingMap.get(arenaName).contains(player)){
                if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                    if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
                        event.setCancelled(true);
                        event.getPlayer().setGameMode(GameMode.ADVENTURE);
                        event.getPlayer().setAllowFlight(true);
                    }
                }
            }
        }
    }
}
