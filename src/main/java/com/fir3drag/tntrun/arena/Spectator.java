package com.fir3drag.tntrun.arena;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Spectator {
    private final TntRun plugin;

    public Spectator(TntRun plugin) {
        this.plugin = plugin;
    }

    // hide this player from all "playing" players, show spectators to this player
    public void setSpectator(String arenaName, Player player){  // custom spectator
        player.setAllowFlight(true);
        hideYouFromPlayers(arenaName, player);
        showSpectators(arenaName, player);
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
}
