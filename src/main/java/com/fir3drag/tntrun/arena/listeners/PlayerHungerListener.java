package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.List;

public class PlayerHungerListener implements Listener {
    private final TntRun plugin;

    public PlayerHungerListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event){
        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            String arenaName = player.getWorld().getName();
            List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
            boolean disableHunger = this.plugin.data.getTntRunConfig().getBoolean("disableHunger");

            if (arenas.contains(arenaName) && disableHunger){
                event.setCancelled(true);
            }
        }
    }
}
