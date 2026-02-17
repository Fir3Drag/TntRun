package com.fir3drag.tntrun.arena.listeners;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.List;

public class WeatherListener implements Listener {
    private final TntRun plugin;

    public WeatherListener(TntRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        boolean disableWeather = this.plugin.data.getTntRunConfig().getBoolean("disableWeather");
        String arenaName = event.getWorld().getName();

        if (arenas.contains(arenaName) && disableWeather){  // checks its an arena
            event.setCancelled(true);
        }
    }
}
