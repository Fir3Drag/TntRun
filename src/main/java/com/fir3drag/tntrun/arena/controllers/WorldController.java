package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WorldController {
    private final TntRun plugin;

    public WorldController(TntRun plugin) {
        this.plugin = plugin;
    }

    public void handleCreatureSpawn(CreatureSpawnEvent event){
        boolean disableCreatureSpawn = this.plugin.data.getTntRunConfig().getBoolean("disableNaturalCreatureSpawn");

        if (disableCreatureSpawn && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)){  // checks its an arena
            event.setCancelled(true);
        }
    }

    public void handlePlayerDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            boolean disableDamage = this.plugin.data.getTntRunConfig().getBoolean("disableDamage");

            if (disableDamage){
                event.setCancelled(true);
            }
        }
    }

    public void handlePlayerHunger(FoodLevelChangeEvent event){
        if (event.getEntity() instanceof Player){
            boolean disableHunger = this.plugin.data.getTntRunConfig().getBoolean("disableHunger");

            if (disableHunger){
                event.setCancelled(true);
            }
        }
    }

    public void handleWeatherChange(WeatherChangeEvent event){
        boolean disableWeather = this.plugin.data.getTntRunConfig().getBoolean("disableWeather");

        if (disableWeather){  // checks its an arena
            event.setCancelled(true);
        }
    }

    public void handlePlayerJoin(Player player){
        if (this.plugin.data.getTntRunConfig().getBoolean("enableNightVision")){
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1073741823, 255, true));
        }
    }
}
