package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemController {
    private final TntRun plugin;

    // lobby
    private final ItemStack joiningCompass;
    private final ItemStack showPlayers;
    private final ItemStack hidePlayers;

    // spectating
    private final ItemStack spectatingCompass;
    private final ItemStack playAgain;
    private final ItemStack showSpectators;
    private final ItemStack hideSpectators;
    private final ItemStack leave;

    public ItemController(TntRun plugin) {
        this.plugin = plugin;

        // lobby
        this.joiningCompass = createJoiningCompass();
        this.showPlayers = createShowPlayers();
        this.hidePlayers = createHidePlayers();

        // spectating
        this.spectatingCompass = createSpectatingCompass();
        this.playAgain = createPlayAgain();
        this.showSpectators = createShowSpectators();
        this.hideSpectators = createHideSpectators();
        this.leave = createLeave();
    }

    // LOBBY ONES
    private ItemStack createJoiningCompass(){
        ItemStack item = new ItemStack(Material.COMPASS, 1);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    this.plugin.data.getTntRunConfig().getString("joiningCompassName")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createShowPlayers(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        item.setDurability((short) 8);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    this.plugin.data.getTntRunConfig().getString("showPlayersName")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createHidePlayers(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        item.setDurability((short) 10);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    this.plugin.data.getTntRunConfig().getString("hidePlayersName")));
            item.setItemMeta(meta);
        }
        return item;
    }

    // SPECTATOR ONES
    private ItemStack createSpectatingCompass(){
        ItemStack item = new ItemStack(Material.COMPASS, 1);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    this.plugin.data.getTntRunConfig().getString("spectatingCompassName")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPlayAgain(){
        ItemStack item = new ItemStack(Material.PAPER, 1);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    this.plugin.data.getTntRunConfig().getString("playAgainName")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createShowSpectators(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        item.setDurability((short) 8);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    this.plugin.data.getTntRunConfig().getString("showSpectatorsName")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createHideSpectators(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        item.setDurability((short) 10);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    this.plugin.data.getTntRunConfig().getString("hideSpectatorsName")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createLeave(){
        ItemStack item = new ItemStack(Material.BED, 1);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    this.plugin.data.getTntRunConfig().getString("leaveName")));
            item.setItemMeta(meta);
        }
        return item;
    }

    // LOBBY
    public ItemStack getJoiningCompass(){
        return joiningCompass;
    }

    public ItemStack getShowPlayers(){
        return showPlayers;
    }

    public ItemStack getHidePlayers(){
        return hidePlayers;
    }

    // SPECTATING
    public ItemStack getSpectatingCompass(){
        return spectatingCompass;
    }

    public ItemStack getPlayAgain(){
        return playAgain;
    }

    public ItemStack getShowSpectators(){
        return showSpectators;
    }

    public ItemStack getHideSpectators(){
        return hideSpectators;
    }

    public ItemStack getLeave(){
        return leave;
    }

    public void handleItems(PlayerInteractEvent event){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (item != null && item.getItemMeta().getDisplayName() != null){
            String actionName = event.getAction().toString();

            if (actionName.contains("LEFT_CLICK") || actionName.contains("RIGHT_CLICK")){
                // LOBBY
                if (item.getItemMeta().getDisplayName().equals(joiningCompass.getItemMeta().getDisplayName())){
                    player.performCommand("tr join");
                }
                else if (item.getItemMeta().getDisplayName().equals(showPlayers.getItemMeta().getDisplayName())){
                    if (this.plugin.lobbyList.contains(player)){
                        this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("lobbyPlayersVisible", true);
                        this.plugin.data.saveConfig();
                        this.plugin.lobbyController.setInventory(player);
                        this.plugin.lobbyController.showPlayers(player);  // show lobby players
                    }
                }
                else if (item.getItemMeta().getDisplayName().equals(hidePlayers.getItemMeta().getDisplayName())){
                    if (this.plugin.lobbyList.contains(player)){
                        this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("lobbyPlayersVisible", false);
                        this.plugin.data.saveConfig();
                        this.plugin.lobbyController.setInventory(player);
                        this.plugin.lobbyController.hidePlayers(player);  // show lobby players
                    }
                }

                // SPECTATING
                else if (item.getItemMeta().getDisplayName().equals(spectatingCompass.getItemMeta().getDisplayName())){  // used to tp to players as a spectator
                    //TODO
                }
                else if (item.getItemMeta().getDisplayName().equals(playAgain.getItemMeta().getDisplayName())){  // used to bring up the inventory where you select arenas
                    player.performCommand("tr join");
                }
                else if ((item.getItemMeta().getDisplayName().equals(showSpectators.getItemMeta().getDisplayName()))){  // used to toggle player visibility
                    if (arenas.contains(arenaName) && this.plugin.spectatingMap.get(arenaName).contains(player)){
                        this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("spectatorPlayersVisible", true);
                        this.plugin.data.saveConfig();
                        this.plugin.spectatorController.setInventory(player);
                        this.plugin.spectatorController.showSpectators(arenaName, player);  // show spectators
                    }
                }
                else if ((item.getItemMeta().getDisplayName().equals(hideSpectators.getItemMeta().getDisplayName()))){  // used to toggle player visibility
                    if (arenas.contains(arenaName) && this.plugin.spectatingMap.get(arenaName).contains(player)){
                        this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("spectatorPlayersVisible", false);
                        this.plugin.data.saveConfig();
                        this.plugin.spectatorController.setInventory(player);
                        this.plugin.spectatorController.hideSpectators(arenaName, player);  // show spectators
                    }
                }
                else if ((item.getItemMeta().getDisplayName().equals(leave.getItemMeta().getDisplayName()))){
                    player.performCommand("leave");
                }
            }
        }
    }
}
