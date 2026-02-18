package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class DisableCommand implements SubCommand {
    private final TntRun plugin;

    public DisableCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.perms.check(commandSender, "tntrun.disable")){
            return;
        }

        if (args.length != 1){
            commandSender.sendMessage(ChatColor.RED + "/tntrun disable [arena]");
            return;
        }
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        List<String> disabledArenas = this.plugin.data.getDataConfig().getStringList("disabledArenas");
        String arenaName = args[0];
        World arena = Bukkit.getWorld(arenaName);

        if (arena == null || !arenas.contains(arenaName)){
            commandSender.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' does not exist.");
            return;
        }

        if (disabledArenas.contains(arenaName)){ // stops adding to the list twice
            commandSender.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' is already disabled.");
            return;
        }

        if (this.plugin.gameStatusMap.get(arenaName).equals("playing")){  // prevents the arena being disabled during a game
            commandSender.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' is in use." +
                    " You can disable during queueing but not whilst the game is running.");
            return;
        }
        // check if the game is queueing, if yes tp all the players out and cancel the countdown
        if (!this.plugin.gameStatusMap.get(arenaName).equals("restarting")){
            for (Player p : new ArrayList<>(this.plugin.playingMap.get(arenaName))) {  // removing from the list so requires new
                Bukkit.broadcastMessage(p.toString());
                p.sendMessage(ChatColor.RED + "Arena has been disabled.");
                p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                this.plugin.playerMaps.removeAll(arenaName, p);
            }
            this.plugin.countdownMap.get(arenaName).cancelCountdown();
        }
        disabledArenas.add(arenaName);
        this.plugin.data.getDataConfig().set("disabledArenas", disabledArenas);
        this.plugin.data.saveConfig();
        commandSender.sendMessage(ChatColor.YELLOW + "Arena '" + arenaName + "' successfully disabled.");
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = this.plugin.data.getDataConfig().getStringList("arenas");
            List<String> disabledArenas = this.plugin.data.getDataConfig().getStringList("disabledArenas");
            List<String> completions = new ArrayList<>();

            for (String arena: disabledArenas){ // remove all disabled arenas from the list
                allCompletions.remove(arena);
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
