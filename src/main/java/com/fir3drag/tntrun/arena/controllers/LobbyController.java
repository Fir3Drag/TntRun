package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LobbyController {
    private final TntRun plugin;

    public LobbyController(TntRun plugin) {
        this.plugin = plugin;
    }

    public void tp(Player player){
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        Bukkit.getScheduler().runTask(plugin, () -> { // prevents fall dmg if it's enabled in the lobby
            player.setVelocity(new Vector(0, 0, 0));
            player.setFallDistance(0f);
        });
    }
}
