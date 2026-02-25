package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.List;

public class LobbyController {
    private final TntRun plugin;

    public LobbyController(TntRun plugin) {
        this.plugin = plugin;
    }

    public void tp(Player player){
        if (!this.plugin.lobbyList.contains(player)){
            this.plugin.lobbyList.add(player);
        }

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        setInventory(player);  // lobby items
        this.plugin.scoreboardController.refresh("lobby", player);  // refresh lobby scoreboard

        if (this.plugin.isShuttingDown){  // prevents run later tasks errors on shutdown
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> { // prevents fall dmg if it's enabled in the lobby
            player.setVelocity(new Vector(0, 0, 0));
            player.setFallDistance(0f);
        });
    }

    public void showPlayers(Player player){
        for (Player p: this.plugin.lobbyList){  // show the lobby players
            if (p != player){
                player.showPlayer(p);
            }
        }
    }

    public void hidePlayers(Player player){
        for (Player p: this.plugin.lobbyList){  // hide the lobby players
            if (p != player){
                player.hidePlayer(p);
            }
        }
    }

    public void setInventory(Player player){
        ItemStack[] invContents = new ItemStack[36];

        // puts the items in the inventory
        invContents[0] = this.plugin.itemController.getJoiningCompass();

        if (this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).getBoolean("lobbyPlayersVisible")){
            invContents[7] = this.plugin.itemController.getHidePlayers();
        }
        else {
            invContents[7] = this.plugin.itemController.getShowPlayers();
        }
        player.getInventory().setContents(invContents);
        player.updateInventory();
    }

    public void handleLobbyInventoryClick(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player){
            Player player = ((Player) event.getWhoClicked()).getPlayer();

            // if their not editing the lobby and their in the lobby cancel the event
            if (!this.plugin.lobbyEditList.contains(player) && this.plugin.lobbyList.contains(player)){
                event.setCancelled(true);
            }
        }
    }

    public void handleJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        String arenaName = player.getWorld().getName();

        if (!arenas.contains(arenaName)) {  // if their not in arena their in lobby
            this.plugin.lobbyList.add(player);
            this.plugin.lobbyController.setInventory(player);
        }
    }

    public void handleQuit(Player player){
        this.plugin.lobbyList.remove(player);  // removes them from lobby lists if their in them
        this.plugin.lobbyEditList.remove(player);  // removes them from lobby lists if their in them
    }

    public void handleBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        // allow you to edit lobby
        if (!this.plugin.lobbyEditList.contains(player) && this.plugin.lobbyList.contains(player)){
            event.setCancelled(true);
        }
    }

    public void handleBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();

        // allow you to edit lobby
        if (!this.plugin.lobbyEditList.contains(player) && this.plugin.lobbyList.contains(player)){
            event.setCancelled(true);
        }
    }
}
