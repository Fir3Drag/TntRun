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

public class FlyCommand implements CommandExecutor, TabCompleter {
    private final TntRun plugin;

    public FlyCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!this.plugin.permController.check(commandSender, "tntrun.fly")){
            return true;
        }

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;

            if (player.getAllowFlight()){
                player.setAllowFlight(false);
                player.sendMessage(ChatColor.YELLOW + "Disabled flight.");
            }
            else {
                player.setAllowFlight(true);
                player.sendMessage(ChatColor.YELLOW + "Enabled flight.");
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
