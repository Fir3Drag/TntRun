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

import java.util.Collections;
import java.util.List;

public class LeaveCommand implements SubCommand {
    private final TntRun plugin;

    public LeaveCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.checkPerms.check(commandSender, "tntrun.leave")){
            return;
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            String arenaName = player.getWorld().getName();
            World arena = player.getWorld();

            if (arenas.contains(arenaName)){  // only runs arena features if in an arena
                // teleport first to prevent getting countdown canceled msgs
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                this.plugin.customSpectator.showAllPlayers(arenaName, player);
                this.plugin.changePlayerMaps.removePlayerAll(arenaName, player);  // removes the player from the lists for the arena their in
                this.plugin.countdown.checkForCancel(arena);  // handles players leaving during start phase
                this.plugin.checkForWinner.check(arena, player);   // handles players leaving during game
            }
            else {
                player.sendMessage(ChatColor.RED + "You are not in an arena.");            }
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Collections.emptyList();
    }
}

