package com.fir3drag.tntrun.configuration;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.entity.Player;

public class DefaultValues {
    private final TntRun plugin;

    public DefaultValues(TntRun plugin) {
        this.plugin = plugin;
    }

    public void handleJoin(Player player){
        // creates the players section
        if (!this.plugin.data.getDataConfig().isConfigurationSection(player.getUniqueId().toString())){
            this.plugin.data.getDataConfig().createSection(player.getUniqueId().toString());
        }

        // check that default values exist
        if (this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).get("lobbyPlayersVisible") == null){
            this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("lobbyPlayersVisible", true);
        }

        if (this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).get("spectatorPlayersVisible") == null){
            this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("spectatorPlayersVisible", true);
        }

        if (this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).get("winCount") == null){
            this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("winCount", 0);
        }
        this.plugin.data.saveConfig();
    }
}
