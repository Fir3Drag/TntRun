package com.fir3drag.tntrun.arena.controllers;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
            meta.setDisplayName(this.plugin.defaultValues.getJoiningCompassName());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createShowPlayers(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        item.setDurability((short) 8);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(this.plugin.defaultValues.getShowPlayersName());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createHidePlayers(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        item.setDurability((short) 10);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(this.plugin.defaultValues.getHidePlayersName());
            item.setItemMeta(meta);
        }
        return item;
    }

    // SPECTATOR ONES
    private ItemStack createSpectatingCompass(){
        ItemStack item = new ItemStack(Material.COMPASS, 1);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(this.plugin.defaultValues.getSpectatingCompassName());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPlayAgain(){
        ItemStack item = new ItemStack(Material.PAPER, 1);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(this.plugin.defaultValues.getPlayAgainName());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createShowSpectators(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        item.setDurability((short) 8);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(this.plugin.defaultValues.getShowSpectatorsName());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createHideSpectators(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        item.setDurability((short) 10);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(this.plugin.defaultValues.getHideSpectatorsName());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createLeave(){
        ItemStack item = new ItemStack(Material.BED, 1);

        ItemMeta meta = item.getItemMeta();

        if (meta != null){
            meta.setDisplayName(this.plugin.defaultValues.getLeaveName());
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
        List<String> arenas = this.plugin.defaultValues.getArenas();
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        String arenaName = player.getWorld().getName();

        if (item != null && item.getItemMeta().getDisplayName() != null){
            String actionName = event.getAction().toString();

            if (actionName.contains("LEFT_CLICK") || actionName.contains("RIGHT_CLICK")){
                // LOBBY
                if (item.getItemMeta().getDisplayName().equals(joiningCompass.getItemMeta().getDisplayName())){
                    player.performCommand("tntrun join");
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
                    this.plugin.gameController.openSpectatingInventory(player);
                }
                else if (item.getItemMeta().getDisplayName().equals(playAgain.getItemMeta().getDisplayName())){  // used to bring up the inventory where you select arenas
                    player.performCommand("tntrun join");
                }
                else if ((item.getItemMeta().getDisplayName().equals(showSpectators.getItemMeta().getDisplayName()))){  // used to toggle player visibility
                    if (arenas.contains(arenaName) && this.plugin.spectatingMap.get(arenaName).contains(player)){
                        this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("spectatorPlayersVisible", true);
                        this.plugin.data.saveConfig();
                        this.plugin.gameController.setSpectatorInventory(player);
                        this.plugin.gameController.showSpectators(arenaName, player);  // show spectators
                    }
                }
                else if ((item.getItemMeta().getDisplayName().equals(hideSpectators.getItemMeta().getDisplayName()))){  // used to toggle player visibility
                    if (arenas.contains(arenaName) && this.plugin.spectatingMap.get(arenaName).contains(player)){
                        this.plugin.data.getDataConfig().getConfigurationSection(player.getUniqueId().toString()).set("spectatorPlayersVisible", false);
                        this.plugin.data.saveConfig();
                        this.plugin.gameController.setSpectatorInventory(player);
                        this.plugin.gameController.hideSpectators(arenaName, player);  // show spectators
                    }
                }
                else if ((item.getItemMeta().getDisplayName().equals(leave.getItemMeta().getDisplayName()))){
                    player.performCommand("leave");
                }
            }
        }
    }

    public void handleItems(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player) {
            Player player = ((Player) event.getWhoClicked()).getPlayer();

            List<String> arenas = this.plugin.defaultValues.getArenas();
            String arenaName = player.getWorld().getName();

            if (arenas.contains(arenaName)) {  // if their in an arena
                if (!this.plugin.editingMap.get(arenaName).contains(player)) {  // and not editing it
                    event.setCancelled(true);
                }
            }

            ItemStack item = event.getCurrentItem();
            String currentWorldName = player.getWorld().getName();
            int maxPlayers = this.plugin.defaultValues.getMaxPlayers();

            // PLAYER HEADS
            if (item != null && item.getItemMeta() != null) {
                String actionName = event.getAction().toString();

                if (item.getType() == Material.SKULL_ITEM && item.getDurability() == (short) 3) {
                    if (item.getItemMeta() instanceof SkullMeta) {
                        // gets the owner of the skull
                        String targetName = ((SkullMeta) item.getItemMeta()).getOwner();
                        Player target = null;

                        // finds the owner in the playing map (makes sure they are playing)
                        for (Player p : this.plugin.playingMap.get(arenaName)) {
                            if (p.getDisplayName().equals(targetName)) {
                                target = p;
                            }
                        }

                        if (target != null) {  // if their found tp to them
                            if (actionName.contains("PICKUP_ALL")) {
                                player.teleport(target);
                                player.closeInventory();
                            } else if (actionName.contains("PICKUP_HALF")) {
                                player.setGameMode(GameMode.SPECTATOR);
                                player.setSpectatorTarget(target);
                                player.closeInventory();
                            }
                        }
                    }
                }

                else if (item.getType() == Material.STAINED_GLASS_PANE && item.getItemMeta().getDisplayName() != null) {
                    arenaName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                    // using the display name of the item to check if it's a valid arena
                    if (arenas.contains(arenaName)) {
                        String arenaState = this.plugin.gameStatusMap.get(arenaName);
                        World arena = Bukkit.getWorld(arenaName);

                        if (arena == null) {
                            player.sendMessage(ChatColor.RED + "Cannot find arena '" + arenaName + "'.");
                            player.closeInventory();
                            return;
                        }

                        if (this.plugin.gameStatusMap.get(arenaName).equals("restarting")) {
                            player.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' is currently restarting.");
                            player.closeInventory();
                            return;
                        }

                        if (this.plugin.playingMap.get(arenaName).contains(player)) {
                            player.sendMessage(ChatColor.RED + "Already connected to arena '" + arenaName + "'.");
                            player.closeInventory();
                            return;
                        }

                        if (this.plugin.gameStatusMap.get(arenaName).equals("starting") && this.plugin.playingMap.get(arenaName).size() >= maxPlayers) {
                            player.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' is full, right click to join as a spectator.");
                            player.closeInventory();
                            return;
                        }

                        // handles the world you were in (done after to make sure msgs are sent correctly, e.g. tp player out of world before the countdown cancel msg appears
                        if (arenas.contains(currentWorldName)) { // current world checks
                            // if the player is already in an arena remove them from the lists before tping them to the new arena
                            this.plugin.playerMapsController.removeAll(currentWorldName, player);
                        }

                        if (actionName.contains("PICKUP_ALL")) {
                            if (arenaState.equals("stopped") || arenaState.equals("starting")) {
                                player.closeInventory();
                                this.plugin.worldController.tpCenterOfBlock(player, arena.getSpawnLocation());  // teleport first to make sure they get the right chat msgs
                                this.plugin.playerMapsController.addToPlaying(arenaName, player);  // adds the player to the playing list
                            }
                        }
                        else if (actionName.contains("PICKUP_HALF") && arenaState.equals("playing")) {
                            player.closeInventory();
                            this.plugin.worldController.tpCenterOfBlock(player, arena.getSpawnLocation());  // teleport first to make sure they get the right chat msgs
                            this.plugin.playerMapsController.addToSpectating(arenaName, player);// adds the player to the spectating list
                        }
                    }
                }
            }
        }
    }
}
