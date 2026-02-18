package com.fir3drag.tntrun.arena;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Lobby {
    private final TntRun plugin;

    public Lobby(TntRun plugin) {
        this.plugin = plugin;
    }

    public void tp(Player player){
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        Bukkit.getScheduler().runTask(plugin, () -> { // prevents fall dmg
            player.setVelocity(new Vector(0, 0, 0));
            player.setFallDistance(0f);
        });
    }
}
