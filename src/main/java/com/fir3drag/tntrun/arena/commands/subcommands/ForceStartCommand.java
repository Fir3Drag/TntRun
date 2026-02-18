package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ForceStartCommand implements SubCommand {
    private final TntRun plugin;

    public ForceStartCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.perms.check(commandSender, "tntrun.forceStart")){
            return;
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        int forceStartCountdown = this.plugin.data.getTntRunConfig().getInt("forceStartCountdown");

        if (commandSender instanceof Player){

            Player player = (Player) commandSender;
            String arenaName = player.getWorld().getName();

            if (this.plugin.playingMap.get(arenaName).size() > 1){
                if (arenas.contains(arenaName)){  // check your in an arena
                    if (this.plugin.countdownMap.get(arenaName).isCounting()){
                        this.plugin.countdownMap.get(arenaName).modifyCountdown(forceStartCountdown);
                    }
                    else {
                        this.plugin.countdownMap.get(arenaName).startCountdown();
                        this.plugin.countdownMap.get(arenaName).modifyCountdown(forceStartCountdown);
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
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Collections.emptyList();
    }

}
