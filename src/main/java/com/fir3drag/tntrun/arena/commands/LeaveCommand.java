package com.fir3drag.tntrun.arena.commands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LeaveCommand implements CommandExecutor, TabCompleter {
    private final TntRun plugin;

    public LeaveCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.leave")){
            return true;
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            Player targetPlayer;
            String arenaName = player.getWorld().getName();

            if (args.length == 1){
                String targetPlayerName = args[0];
                targetPlayer = Bukkit.getPlayerExact(targetPlayerName);

                if (targetPlayer == null){
                    player.sendMessage(ChatColor.RED + "Cannot find player '" + targetPlayerName + "'.");
                    return true;
                }
            }
            else {
                targetPlayer = player;
            }

            // handle being a lobby editor
            if (this.plugin.lobbyEditList.contains(targetPlayer)){
                this.plugin.lobbyEditList.remove(targetPlayer);
                targetPlayer.setGameMode(GameMode.SURVIVAL);
                targetPlayer.sendMessage(ChatColor.YELLOW + "You can not longer edit the lobby.");
            }

            if (arenas.contains(arenaName)){  // only runs arena features if in an arena
                // teleport first to prevent getting countdown canceled msgs
                this.plugin.playerMapsController.removeAll(arenaName, targetPlayer);  // removes the player from the lists for the arena their in
            }
            this.plugin.lobbyController.tp(targetPlayer);
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = new ArrayList<>();
            List<String> completions = new ArrayList<>();

            for (Player p: Bukkit.getOnlinePlayers()){ // convert the list into player names
                allCompletions.add(p.getDisplayName());
            }

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

