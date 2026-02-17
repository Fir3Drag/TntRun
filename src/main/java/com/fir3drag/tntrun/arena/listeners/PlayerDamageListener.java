package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class PlayerDamageListener implements Listener {
    private final TntRun plugin;

    public PlayerDamageListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
           Player player = (Player) event.getEntity();
           String arenaName = player.getWorld().getName();
           List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
           boolean disableDamage = this.plugin.data.getTntRunConfig().getBoolean("disableDamage");

           if (arenas.contains(arenaName) && disableDamage){
               event.setCancelled(true);
           }
        }
    }
}
