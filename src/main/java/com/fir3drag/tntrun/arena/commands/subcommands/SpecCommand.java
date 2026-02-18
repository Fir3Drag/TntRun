package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SpecCommand implements SubCommand {
    private final TntRun plugin;

    public SpecCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.perms.check(commandSender, "tntrun.spec")){
            return;
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;

            if (args.length == 0){  // handles correct arg length
                player.sendMessage(ChatColor.RED + "/tntrun spec [player]");
                return;
            }

            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
            String targetArenaName = null;

            if (targetPlayerName.equalsIgnoreCase(player.getDisplayName())){ // stop you spectating yourself
                player.sendMessage(ChatColor.RED + "You cannot spectator yourself");
                return;
            }

            if (targetPlayer == null){  // prevents invalid player names
                player.sendMessage(ChatColor.RED + "Cannot find player '" + targetPlayerName + "'.");
                return;
            }

            for (String arenaName: arenas){ // loop through playingMap and find if the player is in a game
                if (this.plugin.playingMap.get(arenaName).contains(targetPlayer)){
                    targetArenaName = arenaName;
                    break;
                }
                if (this.plugin.spectatingMap.get(arenaName).contains(targetPlayer)){
                    targetArenaName = arenaName;
                    break;
                }
            }

            if (targetArenaName == null){  // if their not in the playing map
                player.sendMessage(ChatColor.RED + "Player '" + targetPlayerName + "' is not in an arena.");
                return;
            }

            if (this.plugin.spectatingMap.get(targetArenaName).contains(player)) {  // prevent you teleporting into the same arena
                player.sendMessage(ChatColor.RED + "You are already spectating '" + targetPlayerName + "'.");
                return;
            }

            // if your already in an arena remove you as a spectator, player or editor, check for winner and to cancel countdown
            String currentArenaName = player.getWorld().getName();

            if (arenas.contains(currentArenaName)){
                this.plugin.spectator.showAllPlayersYouAndYouAllPlayers(currentArenaName, player);  // shows you all players and all players you
                this.plugin.playerMaps.removeAll(currentArenaName, player);
                this.plugin.winner.checkForWinner(player.getWorld(), player);
                this.plugin.countdown.checkForCancel(player.getWorld());
            }

            // send you to the arena (directly to target player)
            player.teleport(targetPlayer.getLocation());
            this.plugin.playerMaps.addToSpectating(targetArenaName, player);
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args) {
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
