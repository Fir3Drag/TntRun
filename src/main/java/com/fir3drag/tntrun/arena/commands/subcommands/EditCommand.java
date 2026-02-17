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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditCommand implements SubCommand {
    private final TntRun plugin;

    public EditCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.checkPerms.check(commandSender, "tntrun.edit")){
            return;
        }
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        List<String> disabledArenas = this.plugin.data.getDataConfig().getStringList("disabledArenas");

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;

            if (args.length == 0){
                player.sendMessage(ChatColor.RED + "/tntrun edit [arena]");
                return;
            }
            World currentWorld = player.getWorld();
            String currentWorldName = player.getWorld().getName();
            String arenaName = args[0];
            World arena = Bukkit.getWorld(arenaName);

            if (arena == null){  // checks the world exists
                player.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' does not exist.");
                return;
            }

            if (arenas.contains(arenaName)){  // if the arena exists and the player is not in an arena, allow the player to edit and tp them into the world
                // if the world your going to is an arena and it already contains you as an editor then your already in the world
                if (this.plugin.editingMap.get(arenaName).contains(player)){
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
                    this.plugin.customSpectator.showAllPlayers(currentWorldName, player);
                    this.plugin.changePlayerMaps.removePlayerAll(currentWorldName, player);
                    this.plugin.countdown.checkForCancel(currentWorld);
                    this.plugin.checkForWinner.check(currentWorld, player);
                }

                // setup for new arean
                this.plugin.changePlayerMaps.addPlayerToEditing(arenaName, player);  // add to the new editing list of the arena their going to
                player.teleport(arena.getSpawnLocation()); // moves player into arena

                player.sendMessage(ChatColor.YELLOW + "Editing arena '" + arenaName + "'.");

                if (disabledArenas.contains(arenaName)){
                    player.sendMessage(ChatColor.YELLOW + "This arena is currently disabled. use /tntrun enable " + arenaName + " to allow players to join.");
                }
                player.sendMessage(ChatColor.YELLOW + "Type /tntrun leave to return to main world.");
            }
            else {
                player.sendMessage(ChatColor.RED + "You are already editing an arena, /tntrun leave");
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = this.plugin.data.getDataConfig().getStringList("arenas");
            List<String> completions = new ArrayList<>();

            for (String completion: allCompletions){ // dynamically updates the tab list depending on whats written
                if (completion.startsWith(args[0]))
                {
                    completions.add(completion);
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
