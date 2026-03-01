package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class EditCommand implements SubCommand {
    private final TntRun plugin;

    public EditCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.edit")){
            return;
        }
        List<String> arenas = this.plugin.defaultValues.getArenas();
        List<String> disabledArenas = this.plugin.defaultValues.getDisabledArenas();

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            Player targetPlayer;

            if (args.length == 0){
                player.sendMessage(ChatColor.RED + "/tntrun edit [arena] [optional for player]");
                return;
            }
            String currentWorldName = player.getWorld().getName();
            String arenaName = args[0];
            World arena = Bukkit.getWorld(arenaName);

            if (arena == null){  // checks the world exists
                player.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' does not exist.");
                return;
            }

            if (args.length >= 2){
                String targetPlayerName = args[1];
                targetPlayer = Bukkit.getPlayerExact(targetPlayerName);

                if (targetPlayer == null){
                    player.sendMessage(ChatColor.RED + "Cannot find player '" + targetPlayerName + "'.");
                    return;
                }
            }
            else {
                targetPlayer = player;
            }

            if (arenaName.equalsIgnoreCase("lobby")){
                this.plugin.lobbyEditList.add(targetPlayer);
                targetPlayer.setGameMode(GameMode.CREATIVE);
                targetPlayer.sendMessage(ChatColor.YELLOW + "You can now edit the lobby.");
                targetPlayer.sendMessage(ChatColor.YELLOW + "Run /leave to disable your editing ability");
                return;
            }

            if (arenas.contains(arenaName)){  // if the arena exists and the player is not in an arena, allow the player to edit and tp them into the world
                // if the world your going to is an arena and it already contains you as an editor then your already in the world
                if (this.plugin.editingMap.get(arenaName).contains(targetPlayer)){
                    player.sendMessage(ChatColor.RED + "Already editing arena '" + arenaName + "'.");
                    return;
                }

                // checks that the game is stopped
                if (!this.plugin.gameStatusMap.get(arenaName).equals("stopped") && !this.plugin.playingMap.get(arenaName).isEmpty()){
                    player.sendMessage(ChatColor.RED + "You cannot edit the arena '" + arenaName + "' because it is currently being used. " +
                            "Use /tntrun disable " + arenaName + " to prevent players from joining the arena.");
                    return;
                }

                if (arenas.contains(currentWorldName)) {  // checks if the player is already in an arena
                    this.plugin.playerMapsController.removeAll(currentWorldName, targetPlayer);
                }

                // setup for new arean
                this.plugin.playerMapsController.addToEditing(arenaName, targetPlayer);  // add to the new editing list of the arena their going to
                this.plugin.worldController.tpCenterOfBlock(targetPlayer, arena.getSpawnLocation());  // moves player into arena
                targetPlayer.sendMessage(ChatColor.YELLOW + "Editing arena '" + arenaName + "'.");

                if (disabledArenas.contains(arenaName)){
                    targetPlayer.sendMessage(ChatColor.YELLOW + "This arena is currently disabled. use /tntrun enable " + arenaName + " to allow players to join.");
                }
                targetPlayer.sendMessage(ChatColor.YELLOW + "Type /leave to return to main world.");
            }
            else {
                targetPlayer.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' does not exist.");
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> allCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length == 1){
            allCompletions = this.plugin.defaultValues.getArenas();
            completions = new ArrayList<>();

            if (!allCompletions.contains("lobby")){
                allCompletions.add("lobby"); // allows you to edit the lobby
            }

            for (String completion: allCompletions){ // dynamically updates the tab list depending on what's written
                if (completion.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                {
                    completions.add(completion);
                }
            }
            return completions;
        }

        else if (args.length == 2){
            for (Player p: Bukkit.getOnlinePlayers()){ // convert the list into player names
                allCompletions.add(p.getDisplayName());
            }

            for (String completion: allCompletions){ // dynamically updates the tab list depending on whats written
                if (completion.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                {
                    completions.add(completion);
                }
            }
            return completions;
        }

        return completions;
    }
}
