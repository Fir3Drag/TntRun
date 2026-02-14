package com.fir3drag.tntrun.arena;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CustomSpectator {
    private final TntRun plugin;

    public CustomSpectator(TntRun plugin) {
        this.plugin = plugin;
    }

    public void enableSpectator(Player player){  // custom spectator
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR){
            player.setAllowFlight(true);

            for (Player p: this.plugin.playingMap.get(player.getWorld().getName())){  // hides the spectator for all "playing" players
                p.hidePlayer(player);
            }
        }
    }

    public void disableSpectator(Player player){
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR)
        {
            player.setAllowFlight(false);

            for (Player p: player.getWorld().getPlayers()){  // shows the spectator for all players in world
                p.showPlayer(player);
            }
        }
    }
}
