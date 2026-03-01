package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DeleteCommand implements SubCommand {
    private final TntRun plugin;

    public DeleteCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    public void deleteWorldFiles(File worldFolder) { // used to delete world
        if (worldFolder.exists()) {
            File[] files = worldFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFiles(file);
                    }
                    file.delete();
                }
            }
            worldFolder.delete();
        }
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.delete")){
            return;
        }

        List<String> arenas = this.plugin.defaultValues.getArenas();
        List<String> disabledArenas = this.plugin.defaultValues.getDisabledArenas();

        if (args.length == 0){
            commandSender.sendMessage(ChatColor.RED + "/tntrun delete [arena]");
            return;
        }
        String arenaName = args[0];

        if (arenas.contains(arenaName)){
            World arena = Bukkit.getWorld(arenaName);

            if (arena == null){
                commandSender.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' does not exist.");
                return;
            }

            for (Player p : plugin.playingMap.get(arenaName)) {
                plugin.playerMapsController.removeFromPlaying(arenaName, p);
            }
            for (Player p : plugin.spectatingMap.get(arenaName)) {
                plugin.playerMapsController.removeFromSpectating(arenaName, p);  // show all players to you and you to them
            }

            for (Player p: arena.getPlayers()){  // if players in the world send them to default world
                this.plugin.lobbyController.tp(p);
            }
            // cancel any countdown
            this.plugin.startingCountdownMap.get(arenaName).cancelCountdown();

            // updates configs
            arenas.remove(arenaName);
            disabledArenas.remove(arenaName);
            this.plugin.data.getDataConfig().set("arenas", arenas);
            this.plugin.data.getDataConfig().set("disabledArenas", disabledArenas);
            this.plugin.data.saveConfig();

            this.plugin.removeDefaultArenaValues(arenaName);  // update map values
            this.plugin.lobbyController.createJoiningArenaInventory();

            // unloads and deletes world files
            Bukkit.unloadWorld(arenaName, false);
            deleteWorldFiles(arena.getWorldFolder());

            commandSender.sendMessage(ChatColor.YELLOW + "Arena '" + arenaName + "' deleted.");
        }
        else {
            commandSender.sendMessage(ChatColor.RED +"Arena '" + arenaName + "' does not exist.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = this.plugin.defaultValues.getArenas();
            List<String> completions = new ArrayList<>();

            for (String completion: allCompletions){ // dynamically updates the tab list depending on whats written
                if (completion.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                {
                    completions.add(completion);
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
