package com.fir3drag.tntrun.arena.commands;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ForceStartCommand implements CommandExecutor, TabCompleter {
    private final TntRun plugin;

    public ForceStartCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.forceStart")){
            return true;
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        int forceStartCountdown = this.plugin.data.getTntRunConfig().getInt("forceStartCountdown");

        if (commandSender instanceof Player){

            Player player = (Player) commandSender;
            String arenaName = player.getWorld().getName();

            if (arenas.contains(arenaName)){
                if (this.plugin.playingMap.get(arenaName).size() > 1){  // check your in an arena
                    if (this.plugin.startingCountdownMap.get(arenaName).isCounting()){
                        this.plugin.startingCountdownMap.get(arenaName).modifyCountdown(forceStartCountdown);
                    }
                    else {
                        this.plugin.startingCountdownMap.get(arenaName).startCountdown();
                        this.plugin.startingCountdownMap.get(arenaName).modifyCountdown(forceStartCountdown);
                    }
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "There must be at least 2 players in the arena.");
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Collections.emptyList();
    }
}
