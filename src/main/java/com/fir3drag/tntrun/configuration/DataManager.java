package com.fir3drag.tntrun.configuration;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {
    private final TntRun plugin;

    private FileConfiguration tntRunConfig = null;
    private File tntRunFile = null;
    private FileConfiguration dataConfig = null;
    private File dataFile = null;

    public DataManager(TntRun plugin){
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig()
    {
        // data file
        if (this.tntRunFile == null)
        {
            this.tntRunFile = new File(this.plugin.getDataFolder(), "tntrun.yml");
        }
        this.tntRunConfig = YamlConfiguration.loadConfiguration(this.tntRunFile);
        InputStream tntRunDefaultStream = this.plugin.getResource("tntrun.yml");

        if (tntRunDefaultStream != null)
        {
            YamlConfiguration tntRunDefaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(tntRunDefaultStream));
            tntRunConfig.setDefaults(tntRunDefaultConfig);
        }

        // data file
        if (this.dataFile == null)
        {
            this.dataFile = new File(this.plugin.getDataFolder(), "data.yml");
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(this.dataFile);
        InputStream dataDefaultStream = this.plugin.getResource("data.yml");

        if (dataDefaultStream != null)
        {
            YamlConfiguration dataDefaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(dataDefaultStream));
            dataConfig.setDefaults(dataDefaultConfig);
        }
    }

    public FileConfiguration getTntRunConfig()
    {
        if (this.tntRunConfig == null)
        {
            reloadConfig();
        }
        return this.tntRunConfig;
    }

    public FileConfiguration getDataConfig()
    {
        if (this.dataConfig == null)
        {
            reloadConfig();
        }
        return this.dataConfig;
    }

    public void saveConfig()
    {
        // data
        if (this.dataConfig == null || this.dataFile == null)
        {
            return;
        }
        try
        {
            this.getDataConfig().save(this.dataFile);
        }
        catch (IOException e)
        {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.dataFile, e);
        }
    }

    public void saveDefaultConfig()
    {
        // tntRun
        if (this.tntRunFile == null)
        {
            this.tntRunFile = new File(this.plugin.getDataFolder(), "tntrun.yml");

            if (!this.tntRunFile.exists())
            {
                this.plugin.saveResource("tntrun.yml", false);
            }
        }

        // data
        if (this.dataFile == null)
        {
            this.dataFile = new File(this.plugin.getDataFolder(), "data.yml");

            if (!this.dataFile.exists())
            {
                this.plugin.saveResource("data.yml", false);
            }
        }
    }
}

