package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectatorController {
    private final TntRun plugin;

    public SpectatorController(TntRun plugin) {
        this.plugin = plugin;
    }

    // hide this player from all "playing" players, show spectators to this player
    public void setSpectator(String arenaName, Player player){  // custom spectator
        player.setAllowFlight(true);
        player.setGameMode(GameMode.ADVENTURE);
        hideYouFromPlayers(arenaName, player);
        showSpectators(arenaName, player);
        setInventory(player);
    }

    // show this player to all and show all to this player
    public void showAllPlayersYouAndYouAllPlayers(String arenaName, Player player){
        player.setAllowFlight(false);
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

    public void setInventory(Player player){
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
}
